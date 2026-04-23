package io.yerektus.qadam.coreapi.modules.generation.service;

import io.yerektus.qadam.coreapi.modules.generation.client.OpenRouterClient;
import io.yerektus.qadam.coreapi.modules.generation.dto.*;
import io.yerektus.qadam.coreapi.common.rag.VectorStoreService;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GenerationService {

    private final VectorStoreService vectorStoreService;
    private final OpenRouterClient openRouterClient;
    private final RateLimitService rateLimitService;

    public GenerationService(
            VectorStoreService vectorStoreService,
            OpenRouterClient openRouterClient,
            RateLimitService rateLimitService
    ) {
        this.vectorStoreService = vectorStoreService;
        this.openRouterClient = openRouterClient;
        this.rateLimitService = rateLimitService;
    }

    public Mono<GenerationResponse> generate(UUID userId, GenerationRequest request) {
        return rateLimitService.checkLimit(userId)
                .then(vectorStoreService.search(request.userRequest(), userId))
                .flatMap(documents -> {
                    // Build context from retrieved documents
                    String context = documents.stream()
                            .map(doc -> {
                                String fileName = (String) doc.getMetadata().getOrDefault("fileName", "unknown");
                                return "[" + fileName + "]\n" + doc.getText();
                            })
                            .collect(Collectors.joining("\n\n---\n\n"));

                    // Collect used documents info
                    List<UsedDocument> usedDocs = documents.stream()
                            .map(doc -> new UsedDocument(
                                    UUID.fromString((String) doc.getMetadata().getOrDefault("documentId", UUID.randomUUID().toString())),
                                    (String) doc.getMetadata().getOrDefault("fileName", "unknown")
                            ))
                            .distinct()
                            .toList();

                    // Build prompt messages
                    String systemPrompt = """
                            Ты — профессиональный юрист. Используй только предоставленные документы.
                            Если данных недостаточно — сообщи об этом явно. Отвечай на языке запроса.""";

                    String userPrompt = """
                            [КОНТЕКСТ]
                            %s
                            
                            [ТИП ДОКУМЕНТА]: %s
                            
                            [ЗАДАЧА]
                            %s
                            
                            [ДОПОЛНИТЕЛЬНО]
                            %s""".formatted(
                            context,
                            request.documentType(),
                            request.userRequest(),
                            request.additionalContext() != null ? request.additionalContext() : ""
                    );

                    List<Map<String, String>> messages = List.of(
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", userPrompt)
                    );

                    return openRouterClient.complete(messages)
                            .map(response -> {
                                String generatedText = "";
                                int tokensUsed = 0;

                                if (response.choices() != null && !response.choices().isEmpty()) {
                                    generatedText = response.choices().get(0).message().content();
                                }
                                if (response.usage() != null) {
                                    tokensUsed = response.usage().total_tokens();
                                }

                                return new GenerationResponse(generatedText, usedDocs, tokensUsed);
                            });
                });
    }
}

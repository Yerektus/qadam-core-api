package io.yerektus.qadam.coreapi.modules.document.service;

import io.yerektus.qadam.coreapi.modules.document.entity.Document;
import io.yerektus.qadam.coreapi.modules.document.repository.DocumentRepository;
import io.yerektus.qadam.coreapi.common.rag.TextExtractorService;
import io.yerektus.qadam.coreapi.common.rag.VectorStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentProcessingService {

    private static final Logger log = LoggerFactory.getLogger(DocumentProcessingService.class);

    private final DocumentRepository documentRepository;
    private final TextExtractorService textExtractorService;
    private final VectorStoreService vectorStoreService;

    public DocumentProcessingService(
            DocumentRepository documentRepository,
            TextExtractorService textExtractorService,
            VectorStoreService vectorStoreService
    ) {
        this.documentRepository = documentRepository;
        this.textExtractorService = textExtractorService;
        this.vectorStoreService = vectorStoreService;
    }

    /**
     * Processes a document: extracts text, splits into chunks, creates embeddings,
     * and stores in the vector store. Updates document status accordingly.
     */
    public void process(UUID documentId) {
        documentRepository.findById(documentId)
                .flatMap(doc -> textExtractorService.extractText(doc.getFilePath())
                        .flatMap(text -> {
                            // Split into chunks
                            TokenTextSplitter splitter = new TokenTextSplitter(512, 64, 5, 10000, true);
                            List<org.springframework.ai.document.Document> rawDocs = List.of(
                                    new org.springframework.ai.document.Document(text)
                            );
                            List<org.springframework.ai.document.Document> chunks = splitter.apply(rawDocs);

                            // Add metadata to each chunk
                            chunks.forEach(chunk -> {
                                chunk.getMetadata().put("userId", doc.getUserId().toString());
                                chunk.getMetadata().put("projectId", doc.getProjectId().toString());
                                chunk.getMetadata().put("documentId", doc.getId().toString());
                                chunk.getMetadata().put("fileName", doc.getFileName());
                            });

                            return vectorStoreService.addDocuments(chunks);
                        })
                        .then(updateStatus(doc, "READY"))
                        .onErrorResume(e -> {
                            log.error("Failed to process document {}: {}", documentId, e.getMessage(), e);
                            return updateStatus(doc, "FAILED");
                        })
                )
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    private Mono<Document> updateStatus(Document doc, String status) {
        doc.setStatus(status);
        doc.setUpdatedAt(LocalDateTime.now());
        return documentRepository.save(doc);
    }
}

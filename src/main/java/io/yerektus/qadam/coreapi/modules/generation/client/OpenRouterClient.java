package io.yerektus.qadam.coreapi.modules.generation.client;

import io.yerektus.qadam.coreapi.modules.generation.dto.OpenRouterRequest;
import io.yerektus.qadam.coreapi.modules.generation.dto.OpenRouterResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class OpenRouterClient {

    private final WebClient webClient;
    private final String model;

    public OpenRouterClient(
            WebClient openRouterWebClient,
            @Value("${openrouter.model}") String model
    ) {
        this.webClient = openRouterWebClient;
        this.model = model;
    }

    /**
     * Sends a chat completion request to OpenRouter.
     *
     * @param messages list of {role, content} message maps
     * @return OpenRouter response with generated text and usage
     */
    public Mono<OpenRouterResponse> complete(List<Map<String, String>> messages) {
        OpenRouterRequest request = new OpenRouterRequest(model, messages, 4000, 0.3);

        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OpenRouterResponse.class);
    }
}

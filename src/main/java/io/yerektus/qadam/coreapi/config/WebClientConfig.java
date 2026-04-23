package io.yerektus.qadam.coreapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${openrouter.base-url}")
    private String openRouterBaseUrl;

    @Value("${openrouter.api-key}")
    private String openRouterApiKey;

    @Value("${openrouter.app-url}")
    private String openRouterAppUrl;

    @Bean
    public WebClient openRouterWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(openRouterBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openRouterApiKey)
                .defaultHeader("HTTP-Referer", openRouterAppUrl)
                .defaultHeader("X-Title", "Legal Platform")
                .build();
    }
}

package io.yerektus.qadam.coreapi.modules.generation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Response from OpenRouter chat completions API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenRouterResponse(
        List<Choice> choices,
        Usage usage
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Choice(Message message) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Message(String role, String content) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Usage(int prompt_tokens, int completion_tokens, int total_tokens) {}
}

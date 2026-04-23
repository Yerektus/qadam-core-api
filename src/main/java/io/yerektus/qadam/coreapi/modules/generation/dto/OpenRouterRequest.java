package io.yerektus.qadam.coreapi.modules.generation.dto;

import java.util.List;
import java.util.Map;

/**
 * Request body for OpenRouter chat completions API.
 */
public record OpenRouterRequest(
        String model,
        List<Map<String, String>> messages,
        int max_tokens,
        double temperature
) {}

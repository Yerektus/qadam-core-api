package io.yerektus.qadam.coreapi.modules.generation.dto;

import jakarta.validation.constraints.NotBlank;

public record GenerationRequest(
        @NotBlank(message = "Document type is required")
        String documentType,

        @NotBlank(message = "User request is required")
        String userRequest,

        String additionalContext
) {}

package io.yerektus.qadam.coreapi.modules.project.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateProjectEditorRequest(
        @NotNull(message = "Editor content is required")
        String content
) {}

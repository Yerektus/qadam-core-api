package io.yerektus.qadam.coreapi.modules.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(
        @NotBlank(message = "Project name is required")
        @Size(max = 120, message = "Project name is too long")
        String name
) {}

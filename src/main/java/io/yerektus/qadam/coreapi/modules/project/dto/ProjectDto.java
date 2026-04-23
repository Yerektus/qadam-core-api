package io.yerektus.qadam.coreapi.modules.project.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectDto(
        UUID id,
        UUID userId,
        String name,
        LocalDateTime createdAt
) {}

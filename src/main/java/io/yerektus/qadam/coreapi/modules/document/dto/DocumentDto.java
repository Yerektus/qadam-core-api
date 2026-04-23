package io.yerektus.qadam.coreapi.modules.document.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentDto(
        UUID id,
        UUID userId,
        UUID projectId,
        String fileName,
        String description,
        String status,
        LocalDateTime createdAt
) {}

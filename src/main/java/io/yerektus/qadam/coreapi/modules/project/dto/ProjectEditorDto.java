package io.yerektus.qadam.coreapi.modules.project.dto;

import java.util.UUID;

public record ProjectEditorDto(
        UUID projectId,
        String content
) {}

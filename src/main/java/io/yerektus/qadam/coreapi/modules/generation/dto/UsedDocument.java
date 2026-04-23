package io.yerektus.qadam.coreapi.modules.generation.dto;

import java.util.UUID;

public record UsedDocument(
        UUID id,
        String fileName
) {}

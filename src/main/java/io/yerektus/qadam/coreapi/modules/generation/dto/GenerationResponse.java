package io.yerektus.qadam.coreapi.modules.generation.dto;

import java.util.List;

public record GenerationResponse(
        String generatedText,
        List<UsedDocument> usedDocuments,
        int tokensUsed
) {}

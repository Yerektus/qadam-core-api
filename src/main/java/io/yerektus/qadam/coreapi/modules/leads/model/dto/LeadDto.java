package io.yerektus.qadam.coreapi.modules.leads.model.dto;

import java.util.UUID;

public record LeadDto (
    UUID id,
    String email,
    String firstname,
    String lastname,
    String company,
    String city,
    String source,
    String organizationType
) {}

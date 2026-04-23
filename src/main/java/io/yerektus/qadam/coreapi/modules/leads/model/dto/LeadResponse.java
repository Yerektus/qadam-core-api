package io.yerektus.qadam.coreapi.modules.leads.model.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LeadResponse(
    UUID id,
    @JsonProperty("first_name") String firstname,
    @JsonProperty("last_name") String lastname,
    String email,
    String company,
    String city,
    @JsonProperty("organization_type") String organizationType,
    String source
) {}

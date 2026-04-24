package io.yerektus.qadam.coreapi.modules.auth.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccessTokenDto(
    UUID id,
    String token,
    @JsonProperty("expires_at")
    LocalDateTime expiresAt
) {
} 

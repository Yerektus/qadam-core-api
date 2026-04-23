package io.yerektus.qadam.coreapi.modules.auth.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDto(
        UUID id,
        String email,
        String role,
        @JsonProperty("created_at")
        LocalDateTime createdAt
) {}

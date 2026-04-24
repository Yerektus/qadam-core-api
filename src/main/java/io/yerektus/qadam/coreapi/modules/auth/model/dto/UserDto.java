package io.yerektus.qadam.coreapi.modules.auth.model.dto;

import java.util.UUID;

import org.springframework.data.relational.core.mapping.Column;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDto(
        UUID id,
        String email,
        @JsonProperty("first_name")
        String firstname,
        @JsonProperty("last_name")
        String lastname,
        @Column("phone_number")
        String phoneNumber,
        String role
) {}

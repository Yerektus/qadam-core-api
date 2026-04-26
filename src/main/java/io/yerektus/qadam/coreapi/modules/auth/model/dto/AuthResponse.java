package io.yerektus.qadam.coreapi.modules.auth.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(
    @JsonProperty("user")
    UserDto userDto,
    @JsonProperty("auth")
    AccessTokenDto authDto
) {
}

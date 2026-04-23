package io.yerektus.qadam.coreapi.modules.auth.model.dto;

import java.util.UUID;

public record LoginResponse(
        String accessToken,
        UUID userId,
        String email,
        String role
) {}

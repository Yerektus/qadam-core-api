package io.yerektus.qadam.coreapi.modules.auth.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import io.yerektus.qadam.coreapi.modules.auth.model.dto.AccessTokenDto;

@Service
public interface JwtService {
    String generateToken(UUID userId, String role);
    AccessTokenDto generateAuthDto(UUID userId, String role);
    boolean validateToken(String token);
    UUID extractUserId(String token);
    String extractRole(String token);
}

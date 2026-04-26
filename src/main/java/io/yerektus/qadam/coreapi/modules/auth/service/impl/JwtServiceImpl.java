package io.yerektus.qadam.coreapi.modules.auth.service.impl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.yerektus.qadam.coreapi.modules.auth.model.dto.AccessTokenDto;
import io.yerektus.qadam.coreapi.modules.auth.service.JwtService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {
    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtServiceImpl(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(secretBytes);
        this.expirationMs = expirationMs;
    }

    public String generateToken(UUID userId, String role) {
        log.debug("JwtServiceImpl::generateToken started userId={} role={}", userId, role);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        String token = Jwts.builder()
                .subject(userId.toString())
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();

        log.debug("JwtServiceImpl::generateToken completed userId={} role={} expiresAt={}", userId, role, expiry);
        return token;
    }

    public AccessTokenDto generateAuthDto(UUID userId, String role) {
        log.debug("JwtServiceImpl::generateAuthDto started userId={} role={}", userId, role);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        String token = Jwts.builder()
                .subject(userId.toString())
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();

        LocalDateTime expiresAt = expiry.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        AccessTokenDto accessTokenDto = new AccessTokenDto(UUID.randomUUID(), token, expiresAt);
        log.debug("JwtServiceImpl::generateAuthDto completed userId={} role={} accessTokenId={} expiresAt={}",
                userId, role, accessTokenDto.id(), expiresAt);
        return accessTokenDto;
    }

    public boolean validateToken(String token) {
        log.debug("JwtServiceImpl::validateToken started");

        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            log.debug("JwtServiceImpl::validateToken completed");
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JwtServiceImpl::validateToken rejected: invalid token reason={}", e.getClass().getSimpleName());
            return false;
        }
    }

    public UUID extractUserId(String token) {
        log.debug("JwtServiceImpl::extractUserId started");

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        UUID userId = UUID.fromString(claims.getSubject());
        log.debug("JwtServiceImpl::extractUserId completed userId={}", userId);
        return userId;
    }

    public String extractRole(String token) {
        log.debug("JwtServiceImpl::extractRole started");

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String role = claims.get("role", String.class);
        log.debug("JwtServiceImpl::extractRole completed role={}", role);
        return role;
    }
}

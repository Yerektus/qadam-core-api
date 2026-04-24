package io.yerektus.qadam.coreapi.modules.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.yerektus.qadam.coreapi.modules.auth.model.dto.AccessTokenDto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(secretBytes);
        this.expirationMs = expirationMs;
    }

    public String generateToken(UUID userId, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    public AccessTokenDto generateAuthDto(UUID userId, String role) {
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

        return new AccessTokenDto(UUID.randomUUID(), token, expiresAt);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public UUID extractUserId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return UUID.fromString(claims.getSubject());
    }

    public String extractRole(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("role", String.class);
    }
}

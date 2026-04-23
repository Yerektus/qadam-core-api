package io.yerektus.qadam.coreapi.modules.auth.model.entity;

import io.yerektus.qadam.coreapi.common.entity.BaseEntity;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("access_tokens")
public class AccessToken extends BaseEntity {

    @Column("user_id")
    private UUID userId;

    private String token;

    @Column("expires_at")
    private LocalDateTime expiresAt;

    public AccessToken() {}

    public AccessToken(UUID id, UUID userId, String token, LocalDateTime expiresAt, LocalDateTime createdAt) {
        setId(id);
        this.userId = userId;
        this.token = token;
        this.expiresAt = expiresAt;
        setCreatedAt(createdAt);
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}

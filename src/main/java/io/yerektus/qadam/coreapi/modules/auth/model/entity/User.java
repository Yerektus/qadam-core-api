package io.yerektus.qadam.coreapi.modules.auth.model.entity;

import io.yerektus.qadam.coreapi.common.entity.BaseEntity;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("users")
public class User extends BaseEntity {

    @Column("email")
    private String email;

    @Column("password_hash")
    private String passwordHash;

    @Column("role")
    private String role;

    public User() {}

    public User(UUID id, String email, String passwordHash, String role, LocalDateTime createdAt) {
        setId(id);
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        setCreatedAt(createdAt);
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}

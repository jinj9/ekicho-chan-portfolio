package com.ekichochan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_account")
public class AuthAccountEntity {
    @Id
    private UUID id;

    @Column(nullable = false, length = 320)
    private String email;

    @Column(name = "normalized_email", nullable = false, length = 320, unique = true)
    private String normalizedEmail;

    @Column(name = "display_name", nullable = false, length = 30)
    private String displayName;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private AuthAccountStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AuthAccountRole role = AuthAccountRole.USER;

    @Column(name = "email_verified_at")
    private Instant emailVerifiedAt;

    @Column(name = "password_changed_at")
    private Instant passwordChangedAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;

    protected AuthAccountEntity() {
    }

    public AuthAccountEntity(UUID id, String email, String normalizedEmail,
            String displayName, String passwordHash, AuthAccountStatus status) {
        this.id = id;
        this.email = email;
        this.normalizedEmail = normalizedEmail;
        this.displayName = displayName;
        this.passwordHash = passwordHash;
        this.status = status;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getNormalizedEmail() { return normalizedEmail; }
    public String getDisplayName() { return displayName; }
    public String getPasswordHash() { return passwordHash; }
    public AuthAccountStatus getStatus() { return status; }
    public AuthAccountRole getRole() { return role; }
    public Instant getEmailVerifiedAt() { return emailVerifiedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getPasswordChangedAt() { return passwordChangedAt; }

    public void verifyEmail(Instant verifiedAt) {
        if (status != AuthAccountStatus.PENDING_VERIFICATION) {
            throw new IllegalStateException("Account is not pending verification");
        }
        status = AuthAccountStatus.ACTIVE;
        emailVerifiedAt = verifiedAt;
    }

    public void changePassword(String newPasswordHash, Instant changedAt) {
        passwordHash = newPasswordHash;
        passwordChangedAt = changedAt;
    }

    public void changeDisplayName(String newDisplayName) {
        displayName = newDisplayName;
    }
}

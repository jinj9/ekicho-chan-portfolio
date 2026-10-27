package com.ekichochan.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_action_token")
public class AuthActionTokenEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private AuthAccountEntity account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private AuthActionTokenPurpose purpose;

    @Column(name = "token_digest", nullable = false, unique = true)
    private byte[] tokenDigest;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;

    protected AuthActionTokenEntity() {
    }

    public AuthActionTokenEntity(UUID id, AuthAccountEntity account, AuthActionTokenPurpose purpose,
            byte[] tokenDigest, Instant expiresAt) {
        this.id = id;
        this.account = account;
        this.purpose = purpose;
        this.tokenDigest = tokenDigest.clone();
        this.expiresAt = expiresAt;
    }

    public UUID getId() { return id; }
    public AuthAccountEntity getAccount() { return account; }
    public AuthActionTokenPurpose getPurpose() { return purpose; }
    public byte[] getTokenDigest() { return tokenDigest.clone(); }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getUsedAt() { return usedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void markUsed(Instant time) {
        usedAt = time;
    }
}

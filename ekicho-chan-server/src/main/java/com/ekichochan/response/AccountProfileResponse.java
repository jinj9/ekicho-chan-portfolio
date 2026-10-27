package com.ekichochan.response;

import com.ekichochan.entity.AuthAccountEntity;
import java.time.Instant;
import java.util.UUID;

public record AccountProfileResponse(
        UUID accountId, String email, String displayName, Instant createdAt) {
    public static AccountProfileResponse from(AuthAccountEntity account) {
        return new AccountProfileResponse(account.getId(), account.getEmail(),
                account.getDisplayName(), account.getCreatedAt());
    }
}

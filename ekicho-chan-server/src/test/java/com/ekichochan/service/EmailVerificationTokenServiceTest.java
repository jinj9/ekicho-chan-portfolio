package com.ekichochan.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.ekichochan.auth.IssuedVerificationToken;
import com.ekichochan.entity.*;
import com.ekichochan.exception.EmailVerificationException;
import com.ekichochan.repositories.AuthActionTokenRepository;
import java.security.SecureRandom;
import java.time.*;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class EmailVerificationTokenServiceTest {
    private static final Instant NOW = Instant.parse("2026-09-06T12:00:00Z");
    private final AuthActionTokenRepository tokens = mock(AuthActionTokenRepository.class);
    private final SecureRandom random = mock(SecureRandom.class);
    private final Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
    private final EmailVerificationTokenService service =
            new EmailVerificationTokenService(tokens, random, clock);

    @Test
    void issueStoresOnlyDigestWithTwentyFourHourExpiry() {
        doAnswer(invocation -> {
            byte[] bytes = invocation.getArgument(0);
            for (int i = 0; i < bytes.length; i++) bytes[i] = (byte) i;
            return null;
        }).when(random).nextBytes(any(byte[].class));
        AuthAccountEntity account = account();

        IssuedVerificationToken issued = service.issue(account);

        assertEquals(43, issued.rawToken().length());
        ArgumentCaptor<AuthActionTokenEntity> captor =
                ArgumentCaptor.forClass(AuthActionTokenEntity.class);
        verify(tokens).save(captor.capture());
        AuthActionTokenEntity stored = captor.getValue();
        assertArrayEquals(EmailVerificationTokenService.digest(issued.rawToken()), stored.getTokenDigest());
        assertFalse(new String(stored.getTokenDigest()).contains(issued.rawToken()));
        assertEquals(NOW.plus(Duration.ofHours(24)), stored.getExpiresAt());
    }

    @Test
    void validTokenActivatesAccountAndBecomesUsed() {
        AuthAccountEntity account = account();
        AuthActionTokenEntity token = new AuthActionTokenEntity(
                UUID.randomUUID(), account, AuthActionTokenPurpose.VERIFY_EMAIL,
                EmailVerificationTokenService.digest("raw-token"), NOW.plusSeconds(60));
        when(tokens.findForUpdate(any(byte[].class), eq(AuthActionTokenPurpose.VERIFY_EMAIL)))
                .thenReturn(Optional.of(token));

        service.confirm("raw-token");

        assertEquals(AuthAccountStatus.ACTIVE, account.getStatus());
        assertEquals(NOW, account.getEmailVerifiedAt());
        assertEquals(NOW, token.getUsedAt());
    }

    @Test
    void missingExpiredAndUsedTokensUseTheSameError() {
        when(tokens.findForUpdate(any(byte[].class), any())).thenReturn(Optional.empty());
        assertThrows(EmailVerificationException.class, () -> service.confirm("missing"));

        AuthActionTokenEntity expired = new AuthActionTokenEntity(
                UUID.randomUUID(), account(), AuthActionTokenPurpose.VERIFY_EMAIL,
                new byte[32], NOW);
        when(tokens.findForUpdate(any(byte[].class), any())).thenReturn(Optional.of(expired));
        assertThrows(EmailVerificationException.class, () -> service.confirm("expired"));
    }

    private static AuthAccountEntity account() {
        return new AuthAccountEntity(UUID.randomUUID(), "user@example.com", "user@example.com",
                "駅長ちゃん", "$argon2id$hash", AuthAccountStatus.PENDING_VERIFICATION);
    }
}

package com.ekichochan.service;

import com.ekichochan.auth.PasswordResetEmailRequested;
import com.ekichochan.entity.*;
import com.ekichochan.exception.AuthLoginException;
import com.ekichochan.exception.PasswordResetException;
import com.ekichochan.repositories.*;
import java.security.SecureRandom;
import java.time.*;
import java.util.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthPasswordService {
    private final AuthAccountRepository accounts;
    private final AuthActionTokenRepository tokens;
    private final PasswordEncoder encoder;
    private final SecureRandom random;
    private final Clock clock;
    private final ApplicationEventPublisher events;
    private final SessionRevocationService sessions;

    public AuthPasswordService(AuthAccountRepository accounts, AuthActionTokenRepository tokens,
            PasswordEncoder encoder, SecureRandom random, Clock clock,
            ApplicationEventPublisher events, SessionRevocationService sessions) {
        this.accounts = accounts; this.tokens = tokens; this.encoder = encoder;
        this.random = random; this.clock = clock; this.events = events; this.sessions = sessions;
    }

    @Transactional
    public void requestReset(String email) {
        String normalized = email.strip().toLowerCase(Locale.ROOT);
        AuthAccountEntity account = accounts.findByNormalizedEmail(normalized).orElse(null);
        if (account == null || account.getStatus() != AuthAccountStatus.ACTIVE) return;
        Instant now = clock.instant();
        tokens.invalidateUnused(account.getId(), AuthActionTokenPurpose.RESET_PASSWORD, now);
        byte[] bytes = new byte[32]; random.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        tokens.save(new AuthActionTokenEntity(UUID.randomUUID(), account,
                AuthActionTokenPurpose.RESET_PASSWORD,
                EmailVerificationTokenService.digest(raw), now.plus(Duration.ofMinutes(30))));
        events.publishEvent(new PasswordResetEmailRequested(account.getEmail(), raw));
    }

    @Transactional
    public void confirmReset(String rawToken, String newPassword) {
        Instant now = clock.instant();
        AuthActionTokenEntity token = tokens.findForUpdate(
                EmailVerificationTokenService.digest(rawToken), AuthActionTokenPurpose.RESET_PASSWORD)
                .orElseThrow(PasswordResetException::new);
        if (token.getUsedAt() != null || !token.getExpiresAt().isAfter(now)
                || token.getAccount().getStatus() != AuthAccountStatus.ACTIVE) {
            throw new PasswordResetException();
        }
        token.markUsed(now);
        token.getAccount().changePassword(encoder.encode(newPassword), now);
        tokens.invalidateUnused(token.getAccount().getId(), AuthActionTokenPurpose.RESET_PASSWORD, now);
        sessions.revokeAll(token.getAccount().getId());
    }

    @Transactional
    public void changePassword(UUID accountId, String currentPassword, String newPassword) {
        AuthAccountEntity account = accounts.findById(accountId).orElseThrow(AuthLoginException::new);
        if (account.getStatus() != AuthAccountStatus.ACTIVE
                || !encoder.matches(currentPassword, account.getPasswordHash())) {
            throw new AuthLoginException();
        }
        Instant now = clock.instant();
        account.changePassword(encoder.encode(newPassword), now);
        tokens.invalidateUnused(accountId, AuthActionTokenPurpose.RESET_PASSWORD, now);
        sessions.revokeAll(accountId);
    }
}

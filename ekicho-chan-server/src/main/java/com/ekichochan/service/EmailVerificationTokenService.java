package com.ekichochan.service;

import com.ekichochan.auth.IssuedVerificationToken;
import com.ekichochan.entity.*;
import com.ekichochan.exception.EmailVerificationException;
import com.ekichochan.repositories.AuthActionTokenRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailVerificationTokenService {
    private static final Duration LIFETIME = Duration.ofHours(24);
    private final AuthActionTokenRepository tokens;
    private final SecureRandom secureRandom;
    private final Clock clock;

    public EmailVerificationTokenService(
            AuthActionTokenRepository tokens, SecureRandom secureRandom, Clock clock) {
        this.tokens = tokens;
        this.secureRandom = secureRandom;
        this.clock = clock;
    }

    public IssuedVerificationToken issue(AuthAccountEntity account) {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        tokens.save(new AuthActionTokenEntity(
                UUID.randomUUID(), account, AuthActionTokenPurpose.VERIFY_EMAIL,
                digest(rawToken), clock.instant().plus(LIFETIME)));
        return new IssuedVerificationToken(rawToken);
    }

    @Transactional
    public void confirm(String rawToken) {
        Instant now = clock.instant();
        AuthActionTokenEntity token = tokens
                .findForUpdate(digest(rawToken), AuthActionTokenPurpose.VERIFY_EMAIL)
                .orElseThrow(EmailVerificationException::new);
        if (token.getUsedAt() != null || !token.getExpiresAt().isAfter(now)
                || token.getAccount().getStatus() != AuthAccountStatus.PENDING_VERIFICATION) {
            throw new EmailVerificationException();
        }
        token.getAccount().verifyEmail(now);
        token.markUsed(now);
    }

    static byte[] digest(String rawToken) {
        try {
            return MessageDigest.getInstance("SHA-256")
                    .digest(rawToken.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}

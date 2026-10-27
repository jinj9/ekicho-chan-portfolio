package com.ekichochan.service;

import com.ekichochan.auth.IssuedVerificationToken;
import com.ekichochan.auth.VerificationEmailRequested;
import com.ekichochan.entity.AuthAccountEntity;
import com.ekichochan.entity.AuthAccountStatus;
import com.ekichochan.entity.AuthActionTokenEntity;
import com.ekichochan.entity.AuthActionTokenPurpose;
import com.ekichochan.repositories.AuthAccountRepository;
import com.ekichochan.repositories.AuthActionTokenRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailVerificationResendService {
    private static final Duration RESEND_INTERVAL = Duration.ofSeconds(60);

    private final AuthAccountRepository accounts;
    private final AuthActionTokenRepository tokens;
    private final EmailVerificationTokenService verificationTokens;
    private final ApplicationEventPublisher events;
    private final Clock clock;

    public EmailVerificationResendService(AuthAccountRepository accounts,
            AuthActionTokenRepository tokens,
            EmailVerificationTokenService verificationTokens,
            ApplicationEventPublisher events,
            Clock clock) {
        this.accounts = accounts;
        this.tokens = tokens;
        this.verificationTokens = verificationTokens;
        this.events = events;
        this.clock = clock;
    }

    @Transactional
    public void resend(String email) {
        String normalizedEmail = email.strip().toLowerCase(Locale.ROOT);
        AuthAccountEntity account = accounts
                .findByNormalizedEmailForUpdate(normalizedEmail)
                .orElse(null);
        if (account == null || account.getStatus() != AuthAccountStatus.PENDING_VERIFICATION) {
            return;
        }

        Instant now = clock.instant();
        AuthActionTokenEntity latest = tokens
                .findFirstByAccount_IdAndPurposeOrderByCreatedAtDesc(
                        account.getId(), AuthActionTokenPurpose.VERIFY_EMAIL)
                .orElse(null);
        if (latest != null && latest.getCreatedAt() != null
                && latest.getCreatedAt().isAfter(now.minus(RESEND_INTERVAL))) {
            return;
        }

        tokens.invalidateUnused(account.getId(), AuthActionTokenPurpose.VERIFY_EMAIL, now);
        IssuedVerificationToken issued = verificationTokens.issue(account);
        events.publishEvent(new VerificationEmailRequested(account.getEmail(), issued.rawToken()));
    }
}

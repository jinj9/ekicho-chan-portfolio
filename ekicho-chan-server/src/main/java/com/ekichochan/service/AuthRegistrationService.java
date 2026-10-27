package com.ekichochan.service;

import com.ekichochan.entity.AuthAccountEntity;
import com.ekichochan.entity.AuthAccountStatus;
import com.ekichochan.auth.IssuedVerificationToken;
import com.ekichochan.auth.VerificationEmailRequested;
import com.ekichochan.exception.RegistrationInputException;
import com.ekichochan.repositories.AuthAccountRepository;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthRegistrationService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private final AuthAccountRepository accounts;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationTokenService verificationTokens;
    private final ApplicationEventPublisher events;

    public AuthRegistrationService(AuthAccountRepository accounts, PasswordEncoder passwordEncoder,
            EmailVerificationTokenService verificationTokens, ApplicationEventPublisher events) {
        this.accounts = accounts;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokens = verificationTokens;
        this.events = events;
    }

    @Transactional
    public void register(String email, String displayName, String password) {
        String normalizedEmail = email.strip().toLowerCase(Locale.ROOT);
        String normalizedDisplayName = normalizeDisplayName(displayName);
        if (normalizedEmail.length() > 320 || !EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw new RegistrationInputException("INVALID_EMAIL");
        }

        if (accounts.existsByNormalizedEmail(normalizedEmail)) {
            return;
        }

        AuthAccountEntity account = new AuthAccountEntity(
                UUID.randomUUID(),
                normalizedEmail,
                normalizedEmail,
                normalizedDisplayName,
                passwordEncoder.encode(password),
                AuthAccountStatus.PENDING_VERIFICATION);
        try {
            accounts.saveAndFlush(account);
            IssuedVerificationToken token = verificationTokens.issue(account);
            events.publishEvent(new VerificationEmailRequested(account.getEmail(), token.rawToken()));
        } catch (DataIntegrityViolationException exception) {
            // The unique constraint closes the race between the existence check and insert.
            if (!accounts.existsByNormalizedEmail(normalizedEmail)) {
                throw exception;
            }
        }
    }

    private static String normalizeDisplayName(String displayName) {
        String normalized = displayName.strip();
        int length = normalized.codePointCount(0, normalized.length());
        boolean containsControlCharacter = normalized.codePoints().anyMatch(Character::isISOControl);
        if (length < 1 || length > 30 || containsControlCharacter) {
            throw new RegistrationInputException("INVALID_DISPLAY_NAME");
        }
        return normalized;
    }
}

package com.ekichochan.service;

import com.ekichochan.entity.AuthAccountEntity;
import com.ekichochan.entity.AuthAccountStatus;
import com.ekichochan.exception.AuthLoginException;
import com.ekichochan.repositories.AuthAccountRepository;
import com.ekichochan.security.AuthenticatedAccountPrincipal;
import java.util.Locale;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthLoginService {
    private final AuthAccountRepository accounts;
    private final PasswordEncoder passwordEncoder;
    private final String dummyPasswordHash;

    public AuthLoginService(AuthAccountRepository accounts, PasswordEncoder passwordEncoder) {
        this.accounts = accounts;
        this.passwordEncoder = passwordEncoder;
        this.dummyPasswordHash = passwordEncoder.encode(UUID.randomUUID().toString());
    }

    @Transactional(readOnly = true)
    public AuthenticatedAccountPrincipal authenticate(String email, String password) {
        String normalizedEmail = email.strip().toLowerCase(Locale.ROOT);
        AuthAccountEntity account = accounts.findByNormalizedEmail(normalizedEmail).orElse(null);
        String hash = account == null ? dummyPasswordHash : account.getPasswordHash();
        boolean passwordMatches = passwordEncoder.matches(password, hash);
        if (account == null || !passwordMatches || account.getStatus() != AuthAccountStatus.ACTIVE) {
            throw new AuthLoginException();
        }
        return new AuthenticatedAccountPrincipal(
                account.getId(), account.getEmail(), account.getDisplayName(), account.getRole());
    }
}

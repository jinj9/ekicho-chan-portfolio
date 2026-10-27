package com.ekichochan.service;

import com.ekichochan.entity.AuthAccountEntity;
import com.ekichochan.exception.AuthLoginException;
import com.ekichochan.exception.RegistrationInputException;
import com.ekichochan.repositories.AuthAccountRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountProfileService {
    private final AuthAccountRepository accounts;

    public AccountProfileService(AuthAccountRepository accounts) {
        this.accounts = accounts;
    }

    @Transactional(readOnly = true)
    public AuthAccountEntity get(UUID accountId) {
        return accounts.findById(accountId).orElseThrow(AuthLoginException::new);
    }

    @Transactional
    public AuthAccountEntity changeDisplayName(UUID accountId, String displayName) {
        String normalized = displayName.strip();
        int length = normalized.codePointCount(0, normalized.length());
        boolean containsControl = normalized.codePoints().anyMatch(Character::isISOControl);
        if (length < 1 || length > 30 || containsControl) {
            throw new RegistrationInputException("INVALID_DISPLAY_NAME");
        }
        AuthAccountEntity account = accounts.findById(accountId).orElseThrow(AuthLoginException::new);
        account.changeDisplayName(normalized);
        return account;
    }
}

package com.ekichochan.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ekichochan.entity.AuthAccountEntity;
import com.ekichochan.entity.AuthAccountStatus;
import com.ekichochan.exception.AuthLoginException;
import com.ekichochan.repositories.AuthAccountRepository;
import com.ekichochan.repositories.ObtainedImageDataRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AccountDeletionServiceTest {
    @Mock AuthAccountRepository accounts;
    @Mock ObtainedImageDataRepository obtainedImages;
    @Mock PasswordEncoder passwordEncoder;
    @Mock SessionRevocationService sessions;

    private AccountDeletionService service;

    @BeforeEach
    void setUp() {
        service = new AccountDeletionService(accounts, obtainedImages, passwordEncoder, sessions);
    }

    @Test
    void deletesAccountDataWhenPasswordMatches() {
        UUID accountId = UUID.randomUUID();
        AuthAccountEntity account = activeAccount(accountId);
        when(accounts.findByIdForUpdate(accountId)).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("current-password", "password-hash")).thenReturn(true);

        service.delete(accountId, "current-password");

        verify(obtainedImages).deleteByUserId(accountId.toString());
        verify(sessions).revokeAll(accountId);
        verify(accounts).delete(account);
        verify(accounts).flush();
    }

    @Test
    void rejectsDeletionWhenPasswordDoesNotMatch() {
        UUID accountId = UUID.randomUUID();
        AuthAccountEntity account = activeAccount(accountId);
        when(accounts.findByIdForUpdate(accountId)).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("wrong-password", "password-hash")).thenReturn(false);

        assertThrows(AuthLoginException.class,
                () -> service.delete(accountId, "wrong-password"));

        verify(obtainedImages, never()).deleteByUserId(accountId.toString());
        verify(sessions, never()).revokeAll(accountId);
        verify(accounts, never()).delete(account);
    }

    private static AuthAccountEntity activeAccount(UUID id) {
        AuthAccountEntity account = new AuthAccountEntity(id, "test@example.com",
                "test@example.com", "test", "password-hash",
                AuthAccountStatus.PENDING_VERIFICATION);
        account.verifyEmail(Instant.parse("2026-01-01T00:00:00Z"));
        return account;
    }
}

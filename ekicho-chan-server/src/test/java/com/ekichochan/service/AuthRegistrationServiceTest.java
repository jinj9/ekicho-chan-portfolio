package com.ekichochan.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.ekichochan.entity.AuthAccountEntity;
import com.ekichochan.entity.AuthAccountStatus;
import com.ekichochan.auth.IssuedVerificationToken;
import com.ekichochan.exception.RegistrationInputException;
import com.ekichochan.repositories.AuthAccountRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthRegistrationServiceTest {
    private final AuthAccountRepository accounts = mock(AuthAccountRepository.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);
    private final EmailVerificationTokenService verificationTokens = mock(EmailVerificationTokenService.class);
    private final ApplicationEventPublisher events = mock(ApplicationEventPublisher.class);
    private final AuthRegistrationService service = new AuthRegistrationService(
            accounts, encoder, verificationTokens, events);

    @Test
    void storesNormalizedInputAndOnlyThePasswordHash() {
        when(encoder.encode("a long raw password")).thenReturn("$argon2id$encoded");
        when(verificationTokens.issue(any())).thenReturn(new IssuedVerificationToken("raw-token"));
        service.register("  User@Example.COM  ", "  駅長ちゃん  ", "a long raw password");

        ArgumentCaptor<AuthAccountEntity> captor = ArgumentCaptor.forClass(AuthAccountEntity.class);
        verify(accounts).saveAndFlush(captor.capture());
        AuthAccountEntity saved = captor.getValue();
        assertEquals("user@example.com", saved.getEmail());
        assertEquals("user@example.com", saved.getNormalizedEmail());
        assertEquals("駅長ちゃん", saved.getDisplayName());
        assertEquals("$argon2id$encoded", saved.getPasswordHash());
        assertNotEquals("a long raw password", saved.getPasswordHash());
        assertEquals(AuthAccountStatus.PENDING_VERIFICATION, saved.getStatus());
        assertNotNull(saved.getId());
        verify(verificationTokens).issue(saved);
        verify(events).publishEvent(any(Object.class));
    }

    @Test
    void duplicateEmailReturnsWithoutChangingTheExistingAccount() {
        when(accounts.existsByNormalizedEmail("user@example.com")).thenReturn(true);
        service.register("USER@example.com", "別の名前", "another long password");
        verify(accounts, never()).saveAndFlush(any());
        verifyNoInteractions(encoder);
        verifyNoInteractions(verificationTokens, events);
    }

    @Test
    void displayNameUsesUnicodeCodePointsAndRejectsControlCharacters() {
        String thirtyEmoji = "😀".repeat(30);
        when(encoder.encode(any())).thenReturn("hash");
        when(verificationTokens.issue(any())).thenReturn(new IssuedVerificationToken("raw-token"));
        service.register("one@example.com", thirtyEmoji, "a long raw password");

        RegistrationInputException tooLong = assertThrows(RegistrationInputException.class,
                () -> service.register("two@example.com", thirtyEmoji + "😀", "a long raw password"));
        assertEquals("INVALID_DISPLAY_NAME", tooLong.getCode());
        assertThrows(RegistrationInputException.class,
                () -> service.register("three@example.com", "name\nline", "a long raw password"));
    }

    @Test
    void rejectsInvalidEmailAfterTrimming() {
        RegistrationInputException exception = assertThrows(RegistrationInputException.class,
                () -> service.register("not-an-email", "駅長ちゃん", "a long raw password"));
        assertEquals("INVALID_EMAIL", exception.getCode());
        verifyNoInteractions(encoder);
    }
}

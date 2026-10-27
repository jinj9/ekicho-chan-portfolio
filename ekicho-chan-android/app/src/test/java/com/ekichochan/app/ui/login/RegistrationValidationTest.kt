package com.ekichochan.app.ui.login

import org.junit.Assert.*
import org.junit.Test

class RegistrationValidationTest {
    @Test fun acceptsPasswordAtMinimumAndMaximumLength() {
        assertNull(validateRegistration("駅長", "user@example.com", "a".repeat(15), "a".repeat(15)))
        assertNull(validateRegistration("駅長", "user@example.com", "Z".repeat(127) + "!", "Z".repeat(127) + "!"))
    }

    @Test fun rejectsPasswordOutsideLengthRange() {
        assertEquals(RegistrationIssue.PASSWORD_INVALID,
            validateRegistration("駅長", "user@example.com", "a".repeat(14), "a".repeat(14)))
        assertEquals(RegistrationIssue.PASSWORD_INVALID,
            validateRegistration("駅長", "user@example.com", "a".repeat(129), "a".repeat(129)))
    }

    @Test fun rejectsWhitespaceAndNonAsciiPassword() {
        assertEquals(RegistrationIssue.PASSWORD_INVALID,
            validateRegistration("駅長", "user@example.com", "long password 123", "long password 123"))
        assertEquals(RegistrationIssue.PASSWORD_INVALID,
            validateRegistration("駅長", "user@example.com", "パスワードabcdefghijk", "パスワードabcdefghijk"))
    }

    @Test fun rejectsMismatchedConfirmation() {
        assertEquals(RegistrationIssue.PASSWORD_MISMATCH,
            validateRegistration("駅長", "user@example.com", "abcdefghijklmno", "abcdefghijklmnp"))
    }
}

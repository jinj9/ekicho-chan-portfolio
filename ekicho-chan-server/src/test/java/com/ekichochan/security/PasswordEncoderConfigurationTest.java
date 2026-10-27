package com.ekichochan.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class PasswordEncoderConfigurationTest {
    @Test
    void usesArgon2idAndCanVerifyItsOwnHash() {
        PasswordEncoder encoder = new ApiSecurityConfig().passwordEncoder();
        String rawPassword = "a sufficiently long password";
        String hash = encoder.encode(rawPassword);

        assertTrue(hash.startsWith("$argon2id$"));
        assertNotEquals(rawPassword, hash);
        assertTrue(encoder.matches(rawPassword, hash));
        assertFalse(encoder.matches("wrong password", hash));
    }
}

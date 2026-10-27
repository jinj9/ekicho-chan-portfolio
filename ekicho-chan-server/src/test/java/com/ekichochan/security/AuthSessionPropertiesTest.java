package com.ekichochan.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class AuthSessionPropertiesTest {
    @Test void defaultsMatchApprovedTimeouts() {
        var properties = new AuthSessionProperties();
        assertEquals(12 * 60 * 60, properties.webTimeoutSeconds());
        assertEquals(24 * 60 * 60, properties.androidTimeoutSeconds());
    }

    @Test void acceptsShortLocalTimeoutsAndRejectsInvalidValues() {
        var properties = new AuthSessionProperties();
        properties.setWebTimeout(Duration.ofSeconds(20));
        properties.setAndroidTimeout(Duration.ofSeconds(30));
        assertEquals(20, properties.webTimeoutSeconds());
        assertEquals(30, properties.androidTimeoutSeconds());
        assertThrows(IllegalArgumentException.class, () -> properties.setWebTimeout(Duration.ZERO));
    }
}

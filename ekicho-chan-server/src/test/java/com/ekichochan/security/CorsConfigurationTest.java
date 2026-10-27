package com.ekichochan.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.List;
import org.junit.jupiter.api.Test;

class CorsConfigurationTest {
    private final ApiSecurityConfig security = new ApiSecurityConfig();

    @Test void acceptsOnlyConfiguredExactOrigins() {
        var source = security.corsConfigurationSource(List.of("https://www.example.invalid"));
        var configuration = source.getCorsConfiguration(new org.springframework.mock.web.MockHttpServletRequest("GET", "/api/test"));
        assertEquals(List.of("https://www.example.invalid"), configuration.getAllowedOrigins());
        assertEquals(Boolean.TRUE, configuration.getAllowCredentials());
    }

    @Test void rejectsWildcardOrigin() {
        assertThrows(IllegalStateException.class,
                () -> security.corsConfigurationSource(List.of("*")));
    }
}

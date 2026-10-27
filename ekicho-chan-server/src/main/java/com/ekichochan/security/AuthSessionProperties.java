package com.ekichochan.security;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.auth.session")
public class AuthSessionProperties {
    private Duration webTimeout = Duration.ofHours(12);
    private Duration androidTimeout = Duration.ofHours(24);

    public Duration getWebTimeout() { return webTimeout; }
    public void setWebTimeout(Duration value) { webTimeout = requirePositive(value, "web-timeout"); }
    public Duration getAndroidTimeout() { return androidTimeout; }
    public void setAndroidTimeout(Duration value) { androidTimeout = requirePositive(value, "android-timeout"); }
    public int webTimeoutSeconds() { return Math.toIntExact(webTimeout.toSeconds()); }
    public int androidTimeoutSeconds() { return Math.toIntExact(androidTimeout.toSeconds()); }

    private Duration requirePositive(Duration value, String name) {
        if (value == null || value.isZero() || value.isNegative()) {
            throw new IllegalArgumentException("app.auth.session." + name + " must be positive");
        }
        return value;
    }
}

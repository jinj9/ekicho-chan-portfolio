package com.ekichochan.security;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

/** First phase: read-only public API; authentication and writes are not enabled yet. */
@Configuration
public class ApiSecurityConfig {
    @Bean
    SecurityFilterChain apiSecurity(HttpSecurity http,
            UrlBasedCorsConfigurationSource corsConfigurationSource,
            @Value("${app.portfolio-mode:false}") boolean portfolioMode) throws Exception {
        http.authorizeHttpRequests(rules -> {
                if (portfolioMode) {
                    rules.requestMatchers(
                            "/api/v1/auth/**",
                            "/api/v1/web/auth/**",
                            "/api/v1/web/account/**",
                            "/api/v1/web/admin/**",
                            "/api/v1/stations/**").denyAll();
                    rules.requestMatchers(HttpMethod.GET,
                            "/api/v1/portfolio/stations/gacha",
                            "/api/v1/portfolio/stations/*").permitAll();
                    rules.requestMatchers(HttpMethod.POST,
                            "/api/v1/portfolio/stations/location-get").permitAll();
                }
                rules
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                .requestMatchers(HttpMethod.GET,
                        "/actuator/health",
                        "/actuator/health/**").permitAll()
                .requestMatchers(HttpMethod.GET,
                        "/api/stationMaster/store/stationList/*",
                        "/api/stationMaster/search/image/*",
                        "/api/stationMaster/search/*").permitAll()
                .requestMatchers(HttpMethod.POST,
                        "/api/v1/auth/register",
                        "/api/v1/auth/email-verification/resend",
                        "/api/v1/auth/email-verification/confirm",
                        "/api/v1/auth/login",
                        "/api/v1/auth/password-reset/request",
                        "/api/v1/auth/password-reset/confirm").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/web/auth/csrf").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/web/auth/login").permitAll()
                .requestMatchers("/api/v1/web/auth/me", "/api/v1/web/auth/logout").authenticated()
                .requestMatchers(HttpMethod.GET,
                        "/api/v1/web/stations/gacha",
                        "/api/v1/web/stations/obtained",
                        "/api/v1/web/account").authenticated()
                .requestMatchers(HttpMethod.POST,
                        "/api/v1/web/stations/location-get",
                        "/api/v1/web/account/password/change",
                        "/api/v1/web/account/delete").authenticated()
                .requestMatchers(HttpMethod.PUT,
                        "/api/v1/web/account/display-name").authenticated()
                .requestMatchers(HttpMethod.GET,
                        "/api/v1/web/admin/stations/*",
                        "/api/v1/web/admin/stations/*/image").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,
                        "/api/v1/web/admin/stations",
                        "/api/v1/web/admin/stations/search",
                        "/api/v1/web/admin/stations/image").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,
                        "/api/v1/web/admin/stations/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/auth/me").authenticated()
                .requestMatchers(HttpMethod.GET,
                        "/api/v1/stations/gacha",
                        "/api/v1/stations/collection",
                        "/api/v1/stations/collection/*").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/password/change").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/account/delete").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/stations/location-get").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/v1/auth/account/display-name").authenticated()
                // Do not leave legacy userId-based APIs reachable, even with a future session.
                .anyRequest().denyAll();
            })
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .requestCache(AbstractHttpConfigurer::disable)
            // This endpoint is used by the Android client and does not accept cookie authentication.
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                .ignoringRequestMatchers(
                    "/api/v1/auth/register",
                    "/api/v1/auth/email-verification/resend",
                    "/api/v1/auth/email-verification/confirm",
                    "/api/v1/auth/login",
                    "/api/v1/auth/logout",
                    "/api/v1/auth/password-reset/request",
                    "/api/v1/auth/password-reset/confirm",
                    "/api/v1/auth/password/change",
                    "/api/v1/auth/account/delete",
                    "/api/v1/stations/location-get",
                    "/api/v1/portfolio/stations/location-get"))
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .securityContext(context -> context.requireExplicitSave(true))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .exceptionHandling(errors -> errors
                .authenticationEntryPoint((request, response, exception) ->
                    writeError(response, 401, "AUTHENTICATION_REQUIRED"))
                .accessDeniedHandler((request, response, exception) ->
                    writeError(response, 403, "ACCESS_DENIED")));
        // CSRF stays enabled. Session-based login will be added in a separate phase.
        return http.build();
    }

    @Bean
    SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    HttpSessionIdResolver httpSessionIdResolver() {
        return new ClientAwareSessionIdResolver();
    }

    @Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins:http://localhost:3000,http://127.0.0.1:3000}")
            List<String> allowedOrigins) {
        List<String> normalizedOrigins = allowedOrigins.stream()
                .map(String::strip)
                .filter(origin -> !origin.isEmpty())
                .toList();
        if (normalizedOrigins.isEmpty() || normalizedOrigins.contains("*")) {
            throw new IllegalStateException("CORS origins must be explicit and must not contain '*'");
        }
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(normalizedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Content-Type", "X-XSRF-TOKEN", "X-Auth-Token"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Bean
    UserDetailsService noDefaultUser() {
        return username -> { throw new UsernameNotFoundException("Authentication is handled by the API"); };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    SecureRandom secureRandom() {
        return new SecureRandom();
    }

    private static void writeError(HttpServletResponse response, int status, String code) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"code\":\"" + code + "\",\"message\":\"Access is not permitted\"}");
    }
}

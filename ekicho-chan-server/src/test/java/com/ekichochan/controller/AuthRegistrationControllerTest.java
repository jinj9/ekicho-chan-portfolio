package com.ekichochan.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ekichochan.security.ApiSecurityConfig;
import com.ekichochan.service.AuthRegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(properties = "spring.profiles.active=test")
@Import(ApiSecurityConfig.class)
@ContextConfiguration(classes = {
        AuthRegistrationController.class, ApiExceptionHandler.class, ApiSecurityConfig.class
})
class AuthRegistrationControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean AuthRegistrationService registrationService;

    @Test
    void acceptsValidRegistrationWithoutCsrfToken() throws Exception {
        mvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"user@example.com","displayName":"駅長ちゃん",
                     "password":"SufficientlyLong!123"}
                    """))
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.message").isNotEmpty());
        verify(registrationService).register(
                "user@example.com", "駅長ちゃん", "SufficientlyLong!123");
    }

    @Test
    void rejectsInvalidInputBeforeCallingService() throws Exception {
        mvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"", "displayName":"", "password":"short"}
                    """))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(registrationService);
    }

    @Test
    void rejectsPasswordContainingWhitespaceOrNonAsciiCharacters() throws Exception {
        mvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"user@example.com", "displayName":"駅長ちゃん",
                     "password":"長いパスワードPassword123!"}
                    """))
            .andExpect(status().isBadRequest());

        mvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"user@example.com", "displayName":"駅長ちゃん",
                     "password":"Long password 123!"}
                    """))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(registrationService);
    }

    @Test
    void otherAuthenticationEndpointsRemainClosed() throws Exception {
        mvc.perform(post("/api/v1/auth/admin")
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isForbidden());
    }
}

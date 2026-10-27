package com.ekichochan.controller;

import com.ekichochan.request.AuthLoginRequest;
import com.ekichochan.security.AuthSessionProperties;
import com.ekichochan.response.AuthenticatedAccountResponse;
import com.ekichochan.security.AuthenticatedAccountPrincipal;
import com.ekichochan.service.AuthLoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthSessionController {
    private final AuthLoginService loginService;
    private final SecurityContextRepository securityContexts;
    private final AuthSessionProperties sessionProperties;

    public AuthSessionController(AuthLoginService loginService, SecurityContextRepository securityContexts,
            AuthSessionProperties sessionProperties) {
        this.loginService = loginService;
        this.securityContexts = securityContexts;
        this.sessionProperties = sessionProperties;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticatedAccountResponse> login(
            @Valid @RequestBody AuthLoginRequest request,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        AuthenticatedAccountPrincipal principal =
                loginService.authenticate(request.email(), request.password());
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                principal, null, principal.authorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        HttpSession session = servletRequest.getSession(true);
        servletRequest.changeSessionId();
        session.setMaxInactiveInterval(sessionProperties.androidTimeoutSeconds());
        securityContexts.saveContext(context, servletRequest, servletResponse);
        return ResponseEntity.ok()
                .header("X-Auth-Token", session.getId())
                .body(AuthenticatedAccountResponse.from(principal));
    }

    @GetMapping("/me")
    public AuthenticatedAccountResponse me(
            @AuthenticationPrincipal AuthenticatedAccountPrincipal principal) {
        return AuthenticatedAccountResponse.from(principal);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }
}

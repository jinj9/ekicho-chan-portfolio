package com.ekichochan.controller;

import com.ekichochan.request.*;
import com.ekichochan.response.PasswordResetAcceptedResponse;
import com.ekichochan.security.AuthenticatedAccountPrincipal;
import com.ekichochan.service.AuthPasswordService;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthPasswordController {
    private final AuthPasswordService passwords;
    public AuthPasswordController(AuthPasswordService passwords) { this.passwords = passwords; }

    @PostMapping("/password-reset/request")
    public ResponseEntity<PasswordResetAcceptedResponse> requestReset(
            @Valid @RequestBody PasswordResetRequest request) {
        passwords.requestReset(request.email());
        return ResponseEntity.accepted().body(PasswordResetAcceptedResponse.standardResponse());
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<Void> confirmReset(@Valid @RequestBody PasswordResetConfirmRequest request) {
        passwords.confirmReset(request.token(), request.newPassword());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password/change")
    public ResponseEntity<Void> change(@AuthenticationPrincipal AuthenticatedAccountPrincipal principal,
            @Valid @RequestBody PasswordChangeRequest request, HttpServletRequest servletRequest) {
        passwords.changePassword(principal.accountId(), request.currentPassword(), request.newPassword());
        HttpSession session = servletRequest.getSession(false);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }
}

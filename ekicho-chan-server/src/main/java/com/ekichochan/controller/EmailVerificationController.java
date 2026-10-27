package com.ekichochan.controller;

import com.ekichochan.request.EmailVerificationConfirmRequest;
import com.ekichochan.service.EmailVerificationTokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/email-verification")
public class EmailVerificationController {
    private final EmailVerificationTokenService tokens;

    public EmailVerificationController(EmailVerificationTokenService tokens) {
        this.tokens = tokens;
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirm(@Valid @RequestBody EmailVerificationConfirmRequest request) {
        tokens.confirm(request.token());
        return ResponseEntity.noContent().build();
    }
}

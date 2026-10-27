package com.ekichochan.controller;

import com.ekichochan.request.EmailVerificationResendRequest;
import com.ekichochan.service.EmailVerificationResendService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/email-verification")
public class EmailVerificationResendController {
    private final EmailVerificationResendService resendService;

    public EmailVerificationResendController(EmailVerificationResendService resendService) {
        this.resendService = resendService;
    }

    @PostMapping("/resend")
    public ResponseEntity<Void> resend(
            @Valid @RequestBody EmailVerificationResendRequest request) {
        resendService.resend(request.email());
        return ResponseEntity.accepted().build();
    }
}

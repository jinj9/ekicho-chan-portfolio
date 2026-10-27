package com.ekichochan.controller;

import com.ekichochan.request.AuthRegistrationRequest;
import com.ekichochan.response.AuthRegistrationAcceptedResponse;
import com.ekichochan.service.AuthRegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthRegistrationController {
    private final AuthRegistrationService registrationService;

    public AuthRegistrationController(AuthRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthRegistrationAcceptedResponse> register(
            @Valid @RequestBody AuthRegistrationRequest request) {
        registrationService.register(request.email(), request.displayName(), request.password());
        return ResponseEntity.accepted().body(AuthRegistrationAcceptedResponse.standardResponse());
    }
}

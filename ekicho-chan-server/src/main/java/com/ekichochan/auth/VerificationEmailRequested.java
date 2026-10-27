package com.ekichochan.auth;

public record VerificationEmailRequested(String email, String rawToken) {
}

package com.ekichochan.auth;

public record PasswordResetEmailRequested(String email, String rawToken) {
}

package com.ekichochan.request;
import jakarta.validation.constraints.*;
public record PasswordResetConfirmRequest(
        @NotBlank @Size(max=512) String token,
        @NotBlank @Size(min=15,max=128)
        @Pattern(regexp="^[\\x21-\\x7E]+$", message="must contain only ASCII letters, numbers, and symbols")
        String newPassword) {}

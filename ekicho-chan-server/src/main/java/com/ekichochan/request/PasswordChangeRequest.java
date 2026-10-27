package com.ekichochan.request;
import jakarta.validation.constraints.*;
public record PasswordChangeRequest(
        @NotBlank @Size(max=128) String currentPassword,
        @NotBlank @Size(min=15,max=128)
        @Pattern(regexp="^[\\x21-\\x7E]+$", message="must contain only ASCII letters, numbers, and symbols")
        String newPassword) {}

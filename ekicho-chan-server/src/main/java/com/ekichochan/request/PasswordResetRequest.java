package com.ekichochan.request;
import jakarta.validation.constraints.*;
public record PasswordResetRequest(@NotBlank @Size(max=324) String email) {}

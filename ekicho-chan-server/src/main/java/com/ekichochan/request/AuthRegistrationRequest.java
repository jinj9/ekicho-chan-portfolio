package com.ekichochan.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthRegistrationRequest(
        @NotBlank @Size(max = 324) String email,
        @NotBlank @Size(max = 60) String displayName,
        @NotBlank
        @Size(min = 15, max = 128)
        @Pattern(regexp = "^[\\x21-\\x7E]+$", message = "must contain only ASCII letters, numbers, and symbols")
        String password) {
}

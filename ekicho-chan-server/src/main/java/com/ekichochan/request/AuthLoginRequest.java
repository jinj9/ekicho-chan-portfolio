package com.ekichochan.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthLoginRequest(
        @NotBlank @Size(max = 324) String email,
        @NotBlank @Size(max = 128) String password) {
}

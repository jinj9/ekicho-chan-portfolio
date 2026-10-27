package com.ekichochan.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmailVerificationConfirmRequest(@NotBlank @Size(max = 512) String token) {
}

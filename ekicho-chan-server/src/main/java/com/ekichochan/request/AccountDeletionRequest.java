package com.ekichochan.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AccountDeletionRequest(
        @NotBlank @Size(max = 128) String currentPassword) {
}

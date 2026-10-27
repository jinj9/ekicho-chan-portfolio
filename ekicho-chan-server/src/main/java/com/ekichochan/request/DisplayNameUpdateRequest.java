package com.ekichochan.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DisplayNameUpdateRequest(
        @NotBlank @Size(max = 60) String displayName) {
}

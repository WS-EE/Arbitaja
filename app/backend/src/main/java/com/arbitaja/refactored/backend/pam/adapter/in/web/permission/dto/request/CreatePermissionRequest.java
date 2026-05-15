package com.arbitaja.refactored.backend.pam.adapter.in.web.permission.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreatePermissionRequest(
    @NotBlank
    @NotNull
    String name,

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z_-]+$")
    String key) {
}


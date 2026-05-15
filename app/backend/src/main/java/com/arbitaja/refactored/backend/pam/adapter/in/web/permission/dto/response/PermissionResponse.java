package com.arbitaja.refactored.backend.pam.adapter.in.web.permission.dto.response;

import jakarta.validation.constraints.NotNull;

public record PermissionResponse(
    @NotNull
    Integer id,
    @NotNull
    String name,
    @NotNull
    String key
) {
}


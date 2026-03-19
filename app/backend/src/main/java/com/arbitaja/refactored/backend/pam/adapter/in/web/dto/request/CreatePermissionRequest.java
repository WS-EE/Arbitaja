package com.arbitaja.refactored.backend.pam.adapter.in.web.dto.request;

public record CreatePermissionRequest(
        String name,
        String key,
        String keyObject
) {}

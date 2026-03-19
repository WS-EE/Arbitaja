package com.arbitaja.refactored.backend.pam.adapter.in.web.dto.response;

public record PermissionResponse(
    Integer id,
    String name,
    String key,
    String keyObject
) {}

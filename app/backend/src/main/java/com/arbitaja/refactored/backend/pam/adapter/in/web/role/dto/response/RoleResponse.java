package com.arbitaja.refactored.backend.pam.adapter.in.web.role.dto.response;

import com.arbitaja.refactored.backend.pam.core.domain.enums.PermissionCode;

import java.util.List;

public record RoleResponse(
    Integer id,
    String name,
    List<PermissionCode> permissions
) {

}


package com.arbitaja.refactored.backend.pam.adapter.in.web.role.dto.response;

import com.arbitaja.refactored.backend.pam.core.domain.enums.PermissionCode;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RoleResponse(
    @NotNull
    Integer id,
    @NotNull
    String name,
    @NotNull
    List<PermissionCode> permissions
) {

}


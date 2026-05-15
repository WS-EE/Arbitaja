package com.arbitaja.refactored.backend.pam.adapter.in.web.role.dto.request;


import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AddPermissionToRoleRequest(
    @NotNull
    List<Integer> permissionIds
) {
}



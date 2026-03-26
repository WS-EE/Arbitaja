package com.arbitaja.refactored.backend.pam.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;


public record CreateRoleRequest(
    @NotBlank
    String name,
    List<Integer> permissionIds
) {
}

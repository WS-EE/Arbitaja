package com.arbitaja.refactored.backend.pam.adapter.in.web.user.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OverwriteUserRolesRequest(
    @NotNull
    List<Integer> roleIds
) {
}



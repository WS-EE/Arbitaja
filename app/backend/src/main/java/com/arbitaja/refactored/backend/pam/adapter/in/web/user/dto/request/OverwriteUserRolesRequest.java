package com.arbitaja.refactored.backend.pam.adapter.in.web.user.dto.request;

import java.util.List;

public record OverwriteUserRolesRequest(
    List<Integer> roleIds
) {
}



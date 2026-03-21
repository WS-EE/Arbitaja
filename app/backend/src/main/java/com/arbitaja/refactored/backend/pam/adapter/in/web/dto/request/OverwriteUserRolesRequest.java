package com.arbitaja.refactored.backend.pam.adapter.in.web.dto.request;

import java.util.List;

public record OverwriteUserRolesRequest(
        List<Integer> roleIds
) {}


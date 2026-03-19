package com.arbitaja.refactored.backend.pam.adapter.in.web.dto.request;

import java.util.Map;

public record AddPermissionToRoleRequest(
        Integer permissionId,
        Map<String, String> keyObjectAcl
) {}


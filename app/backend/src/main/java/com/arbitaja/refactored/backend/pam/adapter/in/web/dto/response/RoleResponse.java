package com.arbitaja.refactored.backend.pam.adapter.in.web.dto.response;

import com.arbitaja.refactored.backend.pam.core.domain.enums.PermissionCode;
import java.sql.Timestamp;
import java.util.List;

public record RoleResponse(
    Integer id,
    String name,
    Timestamp createdAt,
    Timestamp changedAt,
    List<PermissionCode> permissions
) {

}

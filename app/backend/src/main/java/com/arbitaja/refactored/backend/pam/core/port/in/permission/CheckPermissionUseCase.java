package com.arbitaja.refactored.backend.pam.core.port.in.permission;

import com.arbitaja.refactored.backend.pam.core.domain.enums.PermissionCode;

public interface CheckPermissionUseCase {
    boolean assertUserHasPermissions(String username, PermissionCode[] permissions);
}

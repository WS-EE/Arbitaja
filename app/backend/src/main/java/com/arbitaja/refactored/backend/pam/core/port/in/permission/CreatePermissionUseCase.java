package com.arbitaja.refactored.backend.pam.core.port.in.permission;

import com.arbitaja.refactored.backend.pam.core.domain.model.Permission;
import jakarta.transaction.Transactional;

public interface CreatePermissionUseCase {

    @Transactional
    Permission createPermission(PermissionCommand command);

    @Transactional
    Permission updatePermission(Integer id, PermissionCommand command);


    record PermissionCommand(
            String name,
            String key,
            String keyObject
    ){ }
}

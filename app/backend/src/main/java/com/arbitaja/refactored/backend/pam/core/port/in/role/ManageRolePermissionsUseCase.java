package com.arbitaja.refactored.backend.pam.core.port.in.role;

import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Input port for managing permissions assigned to roles.
 */
public interface ManageRolePermissionsUseCase {

    /**
     * Overwrite all permissions of a role with the provided permission IDs.
     * @param roleId The ID of the role
     * @param permissionIds Full list of permission IDs the role should have after update
     * @return Updated role with synchronized permissions
     */
    @Transactional
    Role overwriteRolePermissions(Integer roleId, List<Integer> permissionIds);
}

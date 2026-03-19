package com.arbitaja.refactored.backend.pam.core.port.in.role;

import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import jakarta.transaction.Transactional;

import java.util.Map;

/**
 * Input port for managing permissions assigned to roles.
 */
public interface ManageRolePermissionsUseCase {

    /**
     * Add a permission to a role
     * @param roleId The ID of the role
     * @param permissionId The ID of the permission to add
     * @param keyObjectAcl Optional ACL map for key object restrictions (can be null)
     * @return Updated role with the new permission
     */
    @Transactional
    Role addPermissionToRole(Integer roleId, Integer permissionId, Map<String, String> keyObjectAcl);

    /**
     * Remove a permission from a role
     * @param roleId The ID of the role
     * @param permissionId The ID of the permission to remove
     * @return Updated role with the permission removed
     */
    @Transactional
    Role removePermissionFromRole(Integer roleId, Integer permissionId);
}


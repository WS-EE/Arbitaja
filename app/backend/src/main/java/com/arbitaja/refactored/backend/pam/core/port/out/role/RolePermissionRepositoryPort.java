package com.arbitaja.refactored.backend.pam.core.port.out.role;

import com.arbitaja.refactored.backend.pam.core.domain.model.RolePermission;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Output port for role permission persistence operations.
 */
public interface RolePermissionRepositoryPort {

    /**
     * Find role permission by ID
     *
     * @param id RolePermission ID
     * @return Optional containing role permission if found
     */
    Optional<RolePermission> findById(@NonNull Integer id);

    /**
     * Find all role permissions
     *
     * @return List of all role permissions
     */
    List<RolePermission> findAll();

    /**
     * Find role permissions by role ID
     *
     * @param roleId Role ID
     * @return List of role permissions for the role
     */
    List<RolePermission> findByRoleId(@NonNull Integer roleId);

    /**
     * Save a role permission
     *
     * @param rolePermission RolePermission to save
     * @return Saved role permission
     */
    RolePermission save(@NonNull RolePermission rolePermission);

    /**
     * Delete a role permission
     *
     * @param rolePermission RolePermission to delete
     */
    void delete(@NonNull RolePermission rolePermission);

    /**
     * Delete role permission by ID
     *
     * @param id RolePermission ID
     */
    void deleteById(@NonNull Integer id);

    /**
     * Bulk delete role permissions by IDs using a direct query (bypasses entity lifecycle).
     *
     * @param ids List of RolePermission IDs to delete
     */
    void deleteByIds(@NonNull List<Integer> ids);
}


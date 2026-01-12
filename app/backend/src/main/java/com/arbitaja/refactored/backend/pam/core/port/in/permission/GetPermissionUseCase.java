package com.arbitaja.refactored.backend.pam.core.port.in.permission;

import com.arbitaja.refactored.backend.pam.core.domain.model.Permission;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Input port for permission query operations.
 */
public interface GetPermissionUseCase {

    /**
     * Get all permissions
     * @return List of all permissions
     */
    List<Permission> getAllPermissions();

    /**
     * Get permission by ID
     * @param id Permission ID
     * @return Optional containing permission if found
     */
    Optional<Permission> getPermissionById(@NonNull Integer id);

    /**
     * Get permissions for a specific user
     * @param userId User ID
     * @return List of permissions assigned to the user
     */
    List<Permission> getPermissionsByUserId(@NonNull Integer userId);
}


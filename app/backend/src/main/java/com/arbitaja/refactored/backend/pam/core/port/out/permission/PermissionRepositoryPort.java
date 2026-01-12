package com.arbitaja.refactored.backend.pam.core.port.out.permission;

import com.arbitaja.refactored.backend.pam.core.domain.model.Permission;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Output port for permission persistence operations.
 */
public interface PermissionRepositoryPort {

    /**
     * Find permission by ID
     * @param id Permission ID
     * @return Optional containing permission if found
     */
    Optional<Permission> findById(@NonNull Integer id);

    /**
     * Find all permissions
     * @return List of all permissions
     */
    List<Permission> findAll();

    /**
     * Find permissions by user ID
     * @param userId User ID
     * @return List of permissions for the user
     */
    List<Permission> findByUserId(@NonNull Integer userId);

    /**
     * Save a permission
     * @param permission Permission to save
     * @return Saved permission
     */
    Permission save(@NonNull Permission permission);

    /**
     * Delete a permission
     * @param permission Permission to delete
     */
    void delete(@NonNull Permission permission);
}


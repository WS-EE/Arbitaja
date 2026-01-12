package com.arbitaja.refactored.backend.pam.core.port.in.role;

import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Input port for role query operations.
 */
public interface GetRoleUseCase {

    /**
     * Get all roles
     * @return List of all roles
     */
    List<Role> getAllRoles();

    /**
     * Get role by ID
     * @param id Role ID
     * @return Optional containing role if found
     */
    Optional<Role> getRoleById(@NonNull Integer id);

    /**
     * Get role by name
     * @param name Role name
     * @return Optional containing role if found
     */
    Optional<Role> getRoleByName(@NonNull String name);

    /**
     * Get roles for a specific user
     * @param userId User ID
     * @return List of roles assigned to the user
     */
    List<Role> getRolesByUserId(@NonNull Integer userId);
}


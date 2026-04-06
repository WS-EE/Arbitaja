package com.arbitaja.refactored.backend.pam.core.port.out.role;

import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Output port for role persistence operations.
 */
public interface RoleRepositoryPort {

    /**
     * Find role by ID
     *
     * @param id Role ID
     * @return Optional containing role if found
     */
    Optional<Role> findById(@NonNull Integer id);

    /**
     * Find role by name
     *
     * @param name Role name
     * @return Optional containing role if found
     */
    Optional<Role> findByName(@NonNull String name);

    /**
     * Find all roles
     *
     * @return List of all roles
     */
    List<Role> findAll();

    /**
     * Find roles by user ID
     *
     * @param userId User ID
     * @return List of roles for the user
     */
    List<Role> findByUserId(@NonNull Integer userId);

    /**
     * Save a role
     *
     * @param role Role to save
     * @return Saved role
     */
    Role save(@NonNull Role role);

    /**
     * Delete a role
     *
     * @param role Role to delete
     */
    void delete(@NonNull Role role);
}


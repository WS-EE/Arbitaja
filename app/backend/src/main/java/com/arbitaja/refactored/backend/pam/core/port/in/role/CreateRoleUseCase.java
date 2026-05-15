package com.arbitaja.refactored.backend.pam.core.port.in.role;

import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Input port for role creation and update operations.
 */
public interface CreateRoleUseCase {

    /**
     * Create a new role
     *
     * @param command Role creation command
     * @return Created role
     */
    @Transactional
    Role createRole(RoleCommand command);

    /**
     * Update an existing role
     *
     * @param id      Role ID
     * @param command Role update command
     * @return Updated role
     */
    @Transactional
    Role updateRole(Integer id, RoleCommand command);

    @Transactional
    void deleteRole(Integer id);

    record RoleCommand(
        String name,
        List<Integer> permissionIds
    ) {
    }
}

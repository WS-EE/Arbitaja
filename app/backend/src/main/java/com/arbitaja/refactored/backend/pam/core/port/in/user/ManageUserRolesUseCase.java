package com.arbitaja.refactored.backend.pam.core.port.in.user;

import com.arbitaja.refactored.backend.pam.core.domain.model.User;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Input port for managing roles assigned to users.
 */
public interface ManageUserRolesUseCase {

    /**
     * Overwrite all roles of a user with the provided role IDs.
     * @param userId The user ID
     * @param roleIds Full list of role IDs the user should have after update
     * @return Updated user with synchronized roles
     */
    @Transactional
    User overwriteUserRoles(Integer userId, List<Integer> roleIds);
}


package com.arbitaja.refactored.backend.pam.core.port.out.role;

import com.arbitaja.refactored.backend.pam.core.domain.model.UserRole;

public interface UserRoleRepositoryPort {

    UserRole saveUserRole(UserRole userRole);

    void deleteUserRole(UserRole userRole);
}

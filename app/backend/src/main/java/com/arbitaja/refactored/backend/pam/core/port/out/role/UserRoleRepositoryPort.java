package com.arbitaja.refactored.backend.pam.core.port.out.role;

import com.arbitaja.refactored.backend.pam.core.domain.model.UserRole;

public interface UserRoleRepositoryPort {

    void saveUserRole(UserRole userRole);
}

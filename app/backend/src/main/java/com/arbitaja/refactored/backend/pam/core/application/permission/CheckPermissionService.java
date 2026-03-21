package com.arbitaja.refactored.backend.pam.core.application.permission;

import com.arbitaja.refactored.backend.pam.core.domain.enums.PermissionCode;
import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.model.User;
import com.arbitaja.refactored.backend.pam.core.port.in.permission.CheckPermissionUseCase;
import com.arbitaja.refactored.backend.pam.core.port.out.permission.PermissionRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.user.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckPermissionService implements CheckPermissionUseCase {

  private final UserRepositoryPort userRepositoryPort;
  private final PermissionRepositoryPort permissionRepositoryPort;

  @Override
  public boolean assertUserHasPermissions(String username, PermissionCode[] permissions) {
    User user = userRepositoryPort.findByUsername(username)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    return permissionRepositoryPort.userHasPermissions(user.getId(), permissions);
  }
}


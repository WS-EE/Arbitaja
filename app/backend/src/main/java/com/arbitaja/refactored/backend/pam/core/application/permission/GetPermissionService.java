package com.arbitaja.refactored.backend.pam.core.application.permission;

import com.arbitaja.refactored.backend.pam.core.domain.model.Permission;
import com.arbitaja.refactored.backend.pam.core.port.in.permission.GetPermissionUseCase;
import com.arbitaja.refactored.backend.pam.core.port.out.permission.PermissionRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Application service implementing permission query use cases.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPermissionService implements GetPermissionUseCase {

    private final PermissionRepositoryPort permissionRepository;

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    public Optional<Permission> getPermissionById(@NonNull Integer id) {
        return permissionRepository.findById(id);
    }

    @Override
    public List<Permission> getPermissionsByUserId(@NonNull Integer userId) {
        return permissionRepository.findByUserId(userId);
    }
}


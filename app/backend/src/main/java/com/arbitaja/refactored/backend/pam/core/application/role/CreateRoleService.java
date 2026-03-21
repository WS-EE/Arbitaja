package com.arbitaja.refactored.backend.pam.core.application.role;

import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.model.Permission;
import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import com.arbitaja.refactored.backend.pam.core.domain.model.RolePermission;
import com.arbitaja.refactored.backend.pam.core.port.in.role.CreateRoleUseCase;
import com.arbitaja.refactored.backend.pam.core.port.out.permission.PermissionRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.role.RolePermissionRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.role.RoleRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Application service implementing role creation and update use cases.
 */
@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class CreateRoleService implements CreateRoleUseCase {

    private final RoleRepositoryPort roleRepository;
    private final PermissionRepositoryPort permissionRepository;
    private final RolePermissionRepositoryPort rolePermissionRepository;

    @Override
    public Role createRole(RoleCommand command) {
        log.info("Creating new role: {}", command.name());

        Role role = Role.createNew(command.name());
        Role savedRole = roleRepository.save(role);

        List<Integer> permissionIds = command.permissionIds();
        if (permissionIds == null || permissionIds.isEmpty()) {
            return savedRole;
        }

        for (Integer permissionId : new LinkedHashSet<>(permissionIds)) {
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new EntityNotFoundException("Permission not found with id: " + permissionId));

            RolePermission rolePermission = RolePermission.createNew(permission, savedRole);
            rolePermissionRepository.save(rolePermission);
            savedRole.getRolePermissions().add(rolePermission);
        }

        return savedRole;
    }

    @Override
    public Role updateRole(Integer id, RoleCommand command) {
        log.info("Updating role with id: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));

        role.setName(command.name());
        role.setChangedAt(new Timestamp(System.currentTimeMillis()));

        return roleRepository.save(role);
    }
}

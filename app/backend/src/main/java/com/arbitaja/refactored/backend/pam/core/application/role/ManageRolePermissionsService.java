package com.arbitaja.refactored.backend.pam.core.application.role;

import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.model.Permission;
import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import com.arbitaja.refactored.backend.pam.core.domain.model.RolePermission;
import com.arbitaja.refactored.backend.pam.core.port.in.role.ManageRolePermissionsUseCase;
import com.arbitaja.refactored.backend.pam.core.port.out.permission.PermissionRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.role.RolePermissionRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.role.RoleRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Application service implementing role permission management use cases.
 */
@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class ManageRolePermissionsService implements ManageRolePermissionsUseCase {

    private final RoleRepositoryPort roleRepository;
    private final PermissionRepositoryPort permissionRepository;
    private final RolePermissionRepositoryPort rolePermissionRepository;

    @Override
    public Role overwriteRolePermissions(Integer roleId, List<Integer> permissionIds) {
        log.info("Overwriting permissions {} for role {}", permissionIds, roleId);

        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));

        Set<Integer> requestedPermissionIds = permissionIds == null
            ? Set.of()
            : new LinkedHashSet<>(permissionIds);

        Set<Integer> existingPermissionIds = role.getRolePermissions().stream()
            .map(rp -> rp.getPermission().getId())
            .collect(Collectors.toSet());

        List<RolePermission> rolePermissionsToRemove = role.getRolePermissions().stream()
            .filter(rp -> !requestedPermissionIds.contains(rp.getPermission().getId()))
            .toList();

        rolePermissionsToRemove.forEach(rolePermission -> {
            role.getRolePermissions().remove(rolePermission);
            rolePermissionRepository.delete(rolePermission);
        });

        for (Integer permissionId : requestedPermissionIds) {
            if (existingPermissionIds.contains(permissionId)) {
                continue;
            }

            Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found with id: " + permissionId));

            RolePermission rolePermission = RolePermission.createNew(permission, role);
            rolePermissionRepository.save(rolePermission);
            role.getRolePermissions().add(rolePermission);
        }

        return roleRepository.save(role);
    }
}

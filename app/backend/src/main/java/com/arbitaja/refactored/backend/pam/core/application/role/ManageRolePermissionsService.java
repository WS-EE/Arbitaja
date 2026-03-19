package com.arbitaja.refactored.backend.pam.core.application.role;

import com.arbitaja.refactored.backend.pam.core.domain.model.Permission;
import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import com.arbitaja.refactored.backend.pam.core.domain.model.RolePermission;
import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.port.in.role.ManageRolePermissionsUseCase;
import com.arbitaja.refactored.backend.pam.core.port.out.permission.PermissionRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.role.RolePermissionRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.role.RoleRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

/**
 * Application service implementing role permission management use cases.
 */
@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class ManageRolePermissionsService implements ManageRolePermissionsUseCase {

    private final RoleRepositoryPort roleRepository;
    private final PermissionRepositoryPort permissionRepository;
    private final RolePermissionRepositoryPort rolePermissionRepository;

    @Override
    public Role addPermissionToRole(Integer roleId, Integer permissionId, Map<String, String> keyObjectAcl) {
        log.info("Adding permission {} to role {}", permissionId, roleId);
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));
        
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found with id: " + permissionId));
        
        // Check if permission already exists for this role
        boolean alreadyExists = role.getRolePermissions().stream()
                .anyMatch(rp -> rp.getPermission().getId().equals(permissionId));
        
        if (!alreadyExists) {
            RolePermission rolePermission = RolePermission.createNew(permission, role, keyObjectAcl);
            rolePermissionRepository.save(rolePermission);
            role.getRolePermissions().add(rolePermission);
            log.info("Successfully added permission {} to role {}", permissionId, roleId);
        } else {
            log.warn("Permission {} already exists for role {}", permissionId, roleId);
        }
        
        return role;
    }

    @Override
    public Role removePermissionFromRole(Integer roleId, Integer permissionId) {
        log.info("Removing permission {} from role {}", permissionId, roleId);
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));
        
        Optional<RolePermission> rolePermissionToRemove = role.getRolePermissions().stream()
                .filter(rp -> rp.getPermission().getId().equals(permissionId))
                .findFirst();
        
        if (rolePermissionToRemove.isPresent()) {
            role.getRolePermissions().remove(rolePermissionToRemove.get());
            rolePermissionRepository.delete(rolePermissionToRemove.get());
            log.info("Successfully removed permission {} from role {}", permissionId, roleId);
        } else {
            log.warn("Permission {} not found for role {}", permissionId, roleId);
        }
        
        return roleRepository.save(role);
    }
}


package com.arbitaja.refactored.backend.pam.core.application.role;

import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.model.Permission;
import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import com.arbitaja.refactored.backend.pam.core.domain.model.RolePermission;
import com.arbitaja.refactored.backend.pam.core.port.out.permission.PermissionRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.role.RolePermissionRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.role.RoleRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageRolePermissionsServiceTest {

    @Mock
    private RoleRepositoryPort roleRepository;

    @Mock
    private PermissionRepositoryPort permissionRepository;

    @Mock
    private RolePermissionRepositoryPort rolePermissionRepository;

    @InjectMocks
    private ManageRolePermissionsService manageRolePermissionsService;

    @Test
    void overwriteRolePermissionsRemovesMissingAndAddsNewOnes() {
        Permission permission1 = Permission.builder().id(1).name("p1").key("P1").build();
        Permission permission2 = Permission.builder().id(2).name("p2").key("P2").build();
        Permission permission3 = Permission.builder().id(3).name("p3").key("P3").build();

        Role role = Role.builder().id(99).name("manager").build();
        RolePermission rp1 = RolePermission.builder().id(11).permission(permission1).role(role).build();
        RolePermission rp2 = RolePermission.builder().id(12).permission(permission2).role(role).build();

        role.setRolePermissions(new LinkedHashSet<>(new ArrayList<>(List.of(rp1, rp2))));

        when(roleRepository.findById(99)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(3)).thenReturn(Optional.of(permission3));
        when(roleRepository.save(role)).thenReturn(role);
        when(rolePermissionRepository.save(any(RolePermission.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Role result = manageRolePermissionsService.overwriteRolePermissions(99, List.of(2, 3, 3));

        Set<Integer> resultingPermissionIds = result.getRolePermissions().stream()
            .map(rolePermission -> rolePermission.getPermission().getId())
            .collect(java.util.stream.Collectors.toSet());

        assertEquals(Set.of(2, 3), resultingPermissionIds);
        verify(rolePermissionRepository).delete(rp1);
        verify(rolePermissionRepository).save(any(RolePermission.class));
        verify(roleRepository).save(role);
    }

    @Test
    void overwriteRolePermissionsThrowsWhenRoleDoesNotExist() {
        when(roleRepository.findById(404)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            manageRolePermissionsService.overwriteRolePermissions(404, List.of(1, 2))
        );
    }
}


package com.arbitaja.refactored.backend.pam.core.application.role;

import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.model.Permission;
import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import com.arbitaja.refactored.backend.pam.core.port.in.role.CreateRoleUseCase;
import com.arbitaja.refactored.backend.pam.core.port.out.permission.PermissionRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.role.RolePermissionRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.role.RoleRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateRoleServiceTest {

    @Mock
    private RoleRepositoryPort roleRepository;

    @Mock
    private PermissionRepositoryPort permissionRepository;

    @Mock
    private RolePermissionRepositoryPort rolePermissionRepository;

    @InjectMocks
    private CreateRoleService createRoleService;

    @Test
    void createRoleSavesRoleAndDistinctPermissions() {
        Role savedRole = Role.builder().id(1).name("manager").build();
        when(roleRepository.save(any(Role.class))).thenReturn(savedRole);
        when(permissionRepository.findById(3)).thenReturn(Optional.of(Permission.builder().id(3).name("View users").key("VIEW_USERS").build()));

        Role result = createRoleService.createRole(new CreateRoleUseCase.RoleCommand("manager", List.of(3, 3)));

        assertEquals(1, result.getId());
        assertEquals(1, result.getRolePermissions().size());
        verify(rolePermissionRepository, times(1)).save(any());
    }

    @Test
    void createRoleThrowsWhenPermissionMissing() {
        when(roleRepository.save(any(Role.class))).thenReturn(Role.builder().id(2).name("editor").build());
        when(permissionRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            createRoleService.createRole(new CreateRoleUseCase.RoleCommand("editor", List.of(99)))
        );
    }

    @Test
    void updateRoleChangesNameAndPersists() {
        Role existing = Role.builder().id(6).name("old").build();
        when(roleRepository.findById(6)).thenReturn(Optional.of(existing));
        when(roleRepository.save(existing)).thenReturn(existing);

        Role result = createRoleService.updateRole(6, new CreateRoleUseCase.RoleCommand("new", List.of()));

        assertEquals("new", result.getName());
        assertNotNull(result.getChangedAt());
        verify(roleRepository).save(existing);
    }

    @Test
    void updateRoleThrowsWhenMissing() {
        when(roleRepository.findById(42)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            createRoleService.updateRole(42, new CreateRoleUseCase.RoleCommand("missing", List.of()))
        );
    }
}


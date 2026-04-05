package com.arbitaja.refactored.backend.pam.core.application.permission;

import com.arbitaja.refactored.backend.pam.core.domain.model.Permission;
import com.arbitaja.refactored.backend.pam.core.port.out.permission.PermissionRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPermissionServiceTest {

    @Mock
    private PermissionRepositoryPort permissionRepository;

    @InjectMocks
    private GetPermissionService getPermissionService;

    @Test
    void getAllPermissionsDelegatesToRepository() {
        List<Permission> permissions = List.of(Permission.builder().id(1).name("View users").key("VIEW_USERS").build());
        when(permissionRepository.findAll()).thenReturn(permissions);

        List<Permission> result = getPermissionService.getAllPermissions();

        assertEquals(1, result.size());
        verify(permissionRepository).findAll();
    }

    @Test
    void getPermissionByIdDelegatesToRepository() {
        Permission permission = Permission.builder().id(7).name("Edit users").key("EDIT_USERS").build();
        when(permissionRepository.findById(7)).thenReturn(Optional.of(permission));

        Optional<Permission> result = getPermissionService.getPermissionById(7);

        assertTrue(result.isPresent());
        assertEquals("EDIT_USERS", result.get().getKey());
    }

    @Test
    void getPermissionsByUserIdDelegatesToRepository() {
        when(permissionRepository.findByUserId(4)).thenReturn(List.of(Permission.builder().id(9).name("View roles").key("VIEW_ROLES").build()));

        List<Permission> result = getPermissionService.getPermissionsByUserId(4);

        assertEquals(1, result.size());
        assertEquals("VIEW_ROLES", result.getFirst().getKey());
    }
}


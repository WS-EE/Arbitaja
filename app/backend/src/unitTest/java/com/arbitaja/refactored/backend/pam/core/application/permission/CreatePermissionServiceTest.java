package com.arbitaja.refactored.backend.pam.core.application.permission;

import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.model.Permission;
import com.arbitaja.refactored.backend.pam.core.port.in.permission.CreatePermissionUseCase;
import com.arbitaja.refactored.backend.pam.core.port.out.permission.PermissionRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreatePermissionServiceTest {

    @Mock
    private PermissionRepositoryPort permissionRepositoryPort;

    @InjectMocks
    private CreatePermissionService createPermissionService;

    @Test
    void createPermissionBuildsDomainObjectAndSavesIt() {
        CreatePermissionUseCase.PermissionCommand command =
            new CreatePermissionUseCase.PermissionCommand("View users", "VIEW_USERS");

        Permission persisted = Permission.builder().id(12).name("View users").key("VIEW_USERS").build();
        when(permissionRepositoryPort.save(any(Permission.class))).thenReturn(persisted);

        Permission result = createPermissionService.createPermission(command);

        assertEquals(12, result.getId());
        assertEquals("View users", result.getName());
        assertEquals("VIEW_USERS", result.getKey());
        verify(permissionRepositoryPort).save(any(Permission.class));
    }

    @Test
    void updatePermissionUpdatesExistingEntityAndPersists() {
        Permission existing = Permission.builder().id(9).name("Old name").key("OLD").build();
        when(permissionRepositoryPort.findById(9)).thenReturn(Optional.of(existing));
        when(permissionRepositoryPort.save(existing)).thenReturn(existing);

        Permission result = createPermissionService.updatePermission(
            9,
            new CreatePermissionUseCase.PermissionCommand("New name", "NEW_KEY")
        );

        assertEquals("New name", result.getName());
        assertEquals("NEW_KEY", result.getKey());
        verify(permissionRepositoryPort).save(existing);
    }

    @Test
    void updatePermissionThrowsWhenPermissionDoesNotExist() {
        when(permissionRepositoryPort.findById(42)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            createPermissionService.updatePermission(
                42,
                new CreatePermissionUseCase.PermissionCommand("Name", "KEY")
            )
        );
    }
}



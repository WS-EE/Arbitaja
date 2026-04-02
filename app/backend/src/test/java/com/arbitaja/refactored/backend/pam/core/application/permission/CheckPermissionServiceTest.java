package com.arbitaja.refactored.backend.pam.core.application.permission;

import com.arbitaja.refactored.backend.pam.core.domain.enums.PermissionCode;
import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.model.User;
import com.arbitaja.refactored.backend.pam.core.port.out.permission.PermissionRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.user.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckPermissionServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PermissionRepositoryPort permissionRepositoryPort;

    @InjectMocks
    private CheckPermissionService checkPermissionService;

    @Test
    void assertUserHasPermissionsReturnsTrueWhenRepositoryConfirmsPermissions() {
        User user = User.builder().id(7).username("alice").saltedPassword("hash").build();
        PermissionCode[] requested = {PermissionCode.VIEW_USERS, PermissionCode.EDIT_USERS};

        when(userRepositoryPort.findByUsername("alice")).thenReturn(Optional.of(user));
        when(permissionRepositoryPort.userHasPermissions(7, requested)).thenReturn(true);

        boolean hasPermissions = checkPermissionService.assertUserHasPermissions("alice", requested);

        assertTrue(hasPermissions);
        verify(userRepositoryPort).findByUsername("alice");
        verify(permissionRepositoryPort).userHasPermissions(7, requested);
    }

    @Test
    void assertUserHasPermissionsReturnsFalseWhenRepositoryRejectsPermissions() {
        User user = User.builder().id(8).username("bob").saltedPassword("hash").build();
        PermissionCode[] requested = {PermissionCode.VIEW_PERMISSIONS};

        when(userRepositoryPort.findByUsername("bob")).thenReturn(Optional.of(user));
        when(permissionRepositoryPort.userHasPermissions(8, requested)).thenReturn(false);

        boolean hasPermissions = checkPermissionService.assertUserHasPermissions("bob", requested);

        assertFalse(hasPermissions);
    }

    @Test
    void assertUserHasPermissionsThrowsWhenUserDoesNotExist() {
        PermissionCode[] requested = {PermissionCode.VIEW_USERS};
        when(userRepositoryPort.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            checkPermissionService.assertUserHasPermissions("missing", requested));

        verify(permissionRepositoryPort, never()).userHasPermissions(org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.any());
    }
}


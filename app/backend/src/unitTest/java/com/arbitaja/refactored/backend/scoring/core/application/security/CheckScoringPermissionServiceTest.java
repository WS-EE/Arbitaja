package com.arbitaja.refactored.backend.scoring.core.application.security;

import com.arbitaja.refactored.backend.scoring.core.domain.enums.ScoringPermissionCode;
import com.arbitaja.refactored.backend.scoring.core.port.out.security.ScoringPermissionGatewayPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckScoringPermissionServiceTest {

    @Mock
    private ScoringPermissionGatewayPort permissionGateway;

    @InjectMocks
    private CheckScoringPermissionService service;

    @Test
    void assertUserHasPermissionsReturnsTrueWhenGatewayConfirmsPermissions() {
        ScoringPermissionCode[] permissions = {ScoringPermissionCode.VIEW_SCORING_DASHBOARD};
        when(permissionGateway.userHasPermissions("alice", permissions)).thenReturn(true);

        boolean result = service.assertUserHasPermissions("alice", permissions);

        assertTrue(result);
        verify(permissionGateway).userHasPermissions("alice", permissions);
    }

    @Test
    void assertUserHasPermissionsReturnsFalseWhenGatewayRejectsPermissions() {
        ScoringPermissionCode[] permissions = {ScoringPermissionCode.MANAGE_SCORING_CRITERIA};
        when(permissionGateway.userHasPermissions("bob", permissions)).thenReturn(false);

        boolean result = service.assertUserHasPermissions("bob", permissions);

        assertFalse(result);
        verify(permissionGateway).userHasPermissions("bob", permissions);
    }

    @Test
    void userIsAdminDelegatesToGateway() {
        when(permissionGateway.userIsAdmin("admin")).thenReturn(true);

        assertTrue(service.userIsAdmin("admin"));
        verify(permissionGateway).userIsAdmin("admin");
    }
}

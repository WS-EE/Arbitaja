package com.arbitaja.refactored.backend.competition.core.application.security;

import com.arbitaja.refactored.backend.competition.core.domain.enums.CompetitionPermissionCode;
import com.arbitaja.refactored.backend.competition.core.port.out.security.CompetitionPermissionGatewayPort;
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
class CheckCompetitionPermissionServiceTest {

    @Mock
    private CompetitionPermissionGatewayPort permissionGateway;

    @InjectMocks
    private CheckCompetitionPermissionService service;

    @Test
    void assertUserHasPermissionsReturnsTrueWhenGatewayConfirmsPermissions() {
        CompetitionPermissionCode[] permissions = {CompetitionPermissionCode.VIEW_COMPETITIONS};
        when(permissionGateway.userHasPermissions("alice", permissions)).thenReturn(true);

        boolean result = service.assertUserHasPermissions("alice", permissions);

        assertTrue(result);
        verify(permissionGateway).userHasPermissions("alice", permissions);
    }

    @Test
    void assertUserHasPermissionsReturnsFalseWhenGatewayRejectsPermissions() {
        CompetitionPermissionCode[] permissions = {CompetitionPermissionCode.CREATE_UPDATE_COMPETITIONS};
        when(permissionGateway.userHasPermissions("bob", permissions)).thenReturn(false);

        boolean result = service.assertUserHasPermissions("bob", permissions);

        assertFalse(result);
        verify(permissionGateway).userHasPermissions("bob", permissions);
    }
}


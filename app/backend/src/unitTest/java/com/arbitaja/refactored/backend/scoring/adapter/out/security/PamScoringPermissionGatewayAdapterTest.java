package com.arbitaja.refactored.backend.scoring.adapter.out.security;

import com.arbitaja.refactored.backend.pam.core.domain.enums.PermissionCode;
import com.arbitaja.refactored.backend.pam.core.port.in.permission.CheckPermissionUseCase;
import com.arbitaja.refactored.backend.scoring.core.domain.enums.ScoringPermissionCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PamScoringPermissionGatewayAdapterTest {

    @Mock
    private CheckPermissionUseCase checkPermissionUseCase;

    @InjectMocks
    private PamScoringPermissionGatewayAdapter adapter;

    @Test
    void userHasPermissionsTranslatesScoringCodesToPamCodes() {
        ScoringPermissionCode[] scoringPermissions = {
            ScoringPermissionCode.VIEW_SCORING_DASHBOARD,
            ScoringPermissionCode.MANAGE_SCORING_CRITERIA
        };
        ArgumentCaptor<PermissionCode[]> captor = ArgumentCaptor.forClass(PermissionCode[].class);
        when(checkPermissionUseCase.assertUserHasPermissions(org.mockito.ArgumentMatchers.eq("alice"), captor.capture()))
            .thenReturn(true);

        boolean result = adapter.userHasPermissions("alice", scoringPermissions);

        assertTrue(result);
        PermissionCode[] passed = captor.getValue();
        assertEquals(2, passed.length);
        assertEquals(PermissionCode.VIEW_SCORING_DASHBOARD, passed[0]);
        assertEquals(PermissionCode.MANAGE_SCORING_CRITERIA, passed[1]);
    }

    @Test
    void userIsAdminChecksAdminPermission() {
        when(checkPermissionUseCase.assertUserHasPermissions(
            org.mockito.ArgumentMatchers.eq("admin"),
            org.mockito.ArgumentMatchers.argThat(p -> p.length == 1 && p[0] == PermissionCode.ADMIN)))
            .thenReturn(true);

        assertTrue(adapter.userIsAdmin("admin"));
    }
}

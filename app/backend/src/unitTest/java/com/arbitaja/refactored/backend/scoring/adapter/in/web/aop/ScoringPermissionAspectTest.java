package com.arbitaja.refactored.backend.scoring.adapter.in.web.aop;

import com.arbitaja.refactored.backend.scoring.adapter.in.web.annotations.RequiresScoringPermission;
import com.arbitaja.refactored.backend.scoring.core.domain.enums.ScoringPermissionCode;
import com.arbitaja.refactored.backend.scoring.core.domain.exception.ForbiddenException;
import com.arbitaja.refactored.backend.scoring.core.port.in.security.CheckScoringPermissionUseCase;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScoringPermissionAspectTest {

    @Mock
    private CheckScoringPermissionUseCase checkPermissionUseCase;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private RequiresScoringPermission requiresScoringPermission;

    @InjectMocks
    private ScoringPermissionAspect aspect;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void checkPermissionProceedsWhenUserHasScoringPermissions() throws Throwable {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("alice", "pw")
        );
        ScoringPermissionCode[] required = {ScoringPermissionCode.VIEW_SCORING_DASHBOARD};

        when(requiresScoringPermission.value()).thenReturn(required);
        when(checkPermissionUseCase.assertUserHasPermissions("alice", required)).thenReturn(true);
        when(joinPoint.proceed()).thenReturn("ok");

        Object result = aspect.checkPermission(joinPoint, requiresScoringPermission);

        assertEquals("ok", result);
        verify(joinPoint).proceed();
    }

    @Test
    void checkPermissionThrowsForbiddenWhenPermissionCheckReturnsFalse() throws Throwable {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("bob", "pw")
        );
        ScoringPermissionCode[] required = {ScoringPermissionCode.MANAGE_SCORING_CRITERIA};

        when(requiresScoringPermission.value()).thenReturn(required);
        when(checkPermissionUseCase.assertUserHasPermissions("bob", required)).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> aspect.checkPermission(joinPoint, requiresScoringPermission));

        verify(joinPoint, never()).proceed();
    }

    @Test
    void checkPermissionThrowsForbiddenWhenAuthenticationMissing() throws Throwable {
        SecurityContextHolder.clearContext();

        assertThrows(ForbiddenException.class, () -> aspect.checkPermission(joinPoint, requiresScoringPermission));

        verify(joinPoint, never()).proceed();
    }

    @Test
    void checkPermissionPassesAllRequestedPermissionsToUseCase() throws Throwable {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("charlie", "pw")
        );
        ScoringPermissionCode[] required = {
            ScoringPermissionCode.VIEW_SCORING_DASHBOARD,
            ScoringPermissionCode.MANAGE_SCORING_CRITERIA,
            ScoringPermissionCode.RECORD_SCORING_RESULTS
        };

        when(requiresScoringPermission.value()).thenReturn(required);
        when(checkPermissionUseCase.assertUserHasPermissions("charlie", required)).thenReturn(true);
        when(joinPoint.proceed()).thenReturn(null);

        aspect.checkPermission(joinPoint, requiresScoringPermission);

        verify(checkPermissionUseCase).assertUserHasPermissions("charlie", required);
    }
}

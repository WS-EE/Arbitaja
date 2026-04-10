package com.arbitaja.refactored.backend.competition.adapter.in.web.aop;

import com.arbitaja.refactored.backend.competition.adapter.in.web.annotations.RequiresCompetitionPermission;
import com.arbitaja.refactored.backend.competition.core.domain.enums.CompetitionPermissionCode;
import com.arbitaja.refactored.backend.competition.core.domain.exception.UnauthorizedException;
import com.arbitaja.refactored.backend.competition.core.port.in.security.CheckCompetitionPermissionUseCase;
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
class CompetitionPermissionAspectTest {

    @Mock
    private CheckCompetitionPermissionUseCase checkPermissionUseCase;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private RequiresCompetitionPermission requiresCompetitionPermission;

    @InjectMocks
    private CompetitionPermissionAspect aspect;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void checkPermissionProceedsWhenUserHasCompetitionPermissions() throws Throwable {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("alice", "pw")
        );
        CompetitionPermissionCode[] required = {CompetitionPermissionCode.VIEW_COMPETITIONS};

        when(requiresCompetitionPermission.value()).thenReturn(required);
        when(checkPermissionUseCase.assertUserHasPermissions("alice", required)).thenReturn(true);
        when(joinPoint.proceed()).thenReturn("ok");

        Object result = aspect.checkPermission(joinPoint, requiresCompetitionPermission);

        assertEquals("ok", result);
        verify(joinPoint).proceed();
    }

    @Test
    void checkPermissionThrowsUnauthorizedWhenPermissionCheckReturnsFalse() throws Throwable {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("bob", "pw")
        );
        CompetitionPermissionCode[] required = {CompetitionPermissionCode.CREATE_UPDATE_COMPETITIONS};

        when(requiresCompetitionPermission.value()).thenReturn(required);
        when(checkPermissionUseCase.assertUserHasPermissions("bob", required)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> aspect.checkPermission(joinPoint, requiresCompetitionPermission));

        verify(joinPoint, never()).proceed();
    }

    @Test
    void checkPermissionPassesAllRequestedCompetitionPermissionsToUseCase() throws Throwable {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("charlie", "pw")
        );
        CompetitionPermissionCode[] required = {
            CompetitionPermissionCode.VIEW_COMPETITIONS,
            CompetitionPermissionCode.CREATE_UPDATE_COMPETITIONS
        };

        when(requiresCompetitionPermission.value()).thenReturn(required);
        when(checkPermissionUseCase.assertUserHasPermissions("charlie", required)).thenReturn(true);
        when(joinPoint.proceed()).thenReturn(null);

        aspect.checkPermission(joinPoint, requiresCompetitionPermission);

        verify(checkPermissionUseCase).assertUserHasPermissions("charlie", required);
    }
}



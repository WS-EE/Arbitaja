package com.arbitaja.refactored.backend.pam.adapter.in.web.aop;

import com.arbitaja.refactored.backend.pam.adapter.in.web.annotations.RequiresPermission;
import com.arbitaja.refactored.backend.pam.core.domain.enums.PermissionCode;
import com.arbitaja.refactored.backend.pam.core.domain.exception.ForbiddenException;
import com.arbitaja.refactored.backend.pam.core.port.in.permission.CheckPermissionUseCase;
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
class PermissionAspectTest {

    @Mock
    private CheckPermissionUseCase checkPermissionUseCase;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private RequiresPermission requiresPermission;

    @InjectMocks
    private PermissionAspect permissionAspect;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void checkPermissionProceedsWhenUserHasRequiredPermissions() throws Throwable {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("alice", "pw")
        );
        PermissionCode[] required = {PermissionCode.VIEW_COMPETITIONS};

        when(requiresPermission.value()).thenReturn(required);
        when(checkPermissionUseCase.assertUserHasPermissions("alice", required)).thenReturn(true);
        when(joinPoint.proceed()).thenReturn("ok");

        Object result = permissionAspect.checkPermission(joinPoint, requiresPermission);

        assertEquals("ok", result);
        verify(joinPoint).proceed();
    }

    @Test
    void checkPermissionThrowsUnauthorizedWhenPermissionCheckReturnsFalse() throws Throwable {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("bob", "pw")
        );
        PermissionCode[] required = {PermissionCode.CREATE_UPDATE_COMPETITIONS};

        when(requiresPermission.value()).thenReturn(required);
        when(checkPermissionUseCase.assertUserHasPermissions("bob", required)).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> permissionAspect.checkPermission(joinPoint, requiresPermission));

        verify(joinPoint, never()).proceed();
    }

    @Test
    void checkPermissionUsesAuthenticatedUsernameForPermissionLookup() throws Throwable {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("charlie", "pw")
        );
        PermissionCode[] required = {PermissionCode.VIEW_USERS};

        when(requiresPermission.value()).thenReturn(required);
        when(checkPermissionUseCase.assertUserHasPermissions("charlie", required)).thenReturn(true);
        when(joinPoint.proceed()).thenReturn(null);

        permissionAspect.checkPermission(joinPoint, requiresPermission);

        verify(checkPermissionUseCase).assertUserHasPermissions("charlie", required);
    }
}


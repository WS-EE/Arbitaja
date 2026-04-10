package com.arbitaja.refactored.backend.pam.adapter.in.web.aop;

import com.arbitaja.refactored.backend.pam.adapter.in.web.annotations.RequiresPermission;
import com.arbitaja.refactored.backend.pam.core.domain.exception.UnauthorizedException;
import com.arbitaja.refactored.backend.pam.core.port.in.permission.CheckPermissionUseCase;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.pam.mode", havingValue = "hex")
public class PermissionAspect {

    private final CheckPermissionUseCase checkPermissionUseCase;

    @Around("@annotation(requiresPermission)")
    public Object checkPermission(
        ProceedingJoinPoint joinPoint,
        RequiresPermission requiresPermission
    ) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean hasPermissions = checkPermissionUseCase.assertUserHasPermissions(auth.getName(), requiresPermission.value());
        if (!hasPermissions) {
            throw new UnauthorizedException("Missing required permissions");
        }
        return joinPoint.proceed();
    }
}

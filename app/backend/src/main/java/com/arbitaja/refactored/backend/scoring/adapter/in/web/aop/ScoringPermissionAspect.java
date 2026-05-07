package com.arbitaja.refactored.backend.scoring.adapter.in.web.aop;

import com.arbitaja.refactored.backend.scoring.adapter.in.web.annotations.RequiresScoringPermission;
import com.arbitaja.refactored.backend.scoring.core.domain.exception.ForbiddenException;
import com.arbitaja.refactored.backend.scoring.core.port.in.security.CheckScoringPermissionUseCase;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Scoring-specific permission guard delegating checks to PAM.
 */
@Aspect
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.scoring.mode", havingValue = "hex", matchIfMissing = true)
public class ScoringPermissionAspect {

    private final CheckScoringPermissionUseCase checkPermissionUseCase;

    @Around("@annotation(requiresScoringPermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequiresScoringPermission requiresScoringPermission) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new ForbiddenException("Authentication is required");
        }

        boolean hasPermissions = checkPermissionUseCase.assertUserHasPermissions(auth.getName(), requiresScoringPermission.value());
        if (!hasPermissions) {
            throw new ForbiddenException("Missing required scoring permissions");
        }

        return joinPoint.proceed();
    }
}

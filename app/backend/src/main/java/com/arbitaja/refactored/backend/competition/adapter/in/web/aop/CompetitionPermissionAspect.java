package com.arbitaja.refactored.backend.competition.adapter.in.web.aop;

import com.arbitaja.refactored.backend.competition.adapter.in.web.annotations.RequiresCompetitionPermission;
import com.arbitaja.refactored.backend.competition.core.domain.exception.UnauthorizedException;
import com.arbitaja.refactored.backend.competition.core.port.in.security.CheckCompetitionPermissionUseCase;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Competition-specific permission guard delegating checks to PAM.
 */
@Aspect
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.competition.mode", havingValue = "hex")
public class CompetitionPermissionAspect {

	private final CheckCompetitionPermissionUseCase checkPermissionUseCase;

	@Around("@annotation(requiresCompetitionPermission)")
	public Object checkPermission(
		ProceedingJoinPoint joinPoint,
		RequiresCompetitionPermission requiresCompetitionPermission
	) throws Throwable {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean hasPermissions = checkPermissionUseCase.assertUserHasPermissions(auth.getName(), requiresCompetitionPermission.value());
		if (!hasPermissions) {
			throw new UnauthorizedException("Missing required competition permissions");
		}

		return joinPoint.proceed();
	}
}



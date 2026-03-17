package com.arbitaja.refactored.backend.pam.adapter.in.web.aop;

import com.arbitaja.refactored.backend.pam.adapter.in.web.annotations.RequiresPermission;
import com.arbitaja.refactored.backend.pam.core.port.in.permission.CheckPermissionUseCase;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

  private final CheckPermissionUseCase checkPermissionUseCase;

  @Around("@annotation(requiresPermission)")
  public Object checkPermission(
      ProceedingJoinPoint joinPoint,
      RequiresPermission requiresPermission
  ) throws Throwable {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    checkPermissionUseCase.assertUserHasPermissions(auth.getName(), requiresPermission.value());
    return joinPoint.proceed();
  }
}

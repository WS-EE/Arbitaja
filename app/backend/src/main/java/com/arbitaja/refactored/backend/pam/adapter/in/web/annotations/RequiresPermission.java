package com.arbitaja.refactored.backend.pam.adapter.in.web.annotations;

import com.arbitaja.refactored.backend.pam.core.domain.enums.PermissionCode;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import lombok.NonNull;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {
  @NonNull PermissionCode[] value();
}

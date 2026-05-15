package com.arbitaja.refactored.backend.scoring.adapter.in.web.annotations;

import com.arbitaja.refactored.backend.scoring.core.domain.enums.ScoringPermissionCode;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresScoringPermission {
    @NotNull ScoringPermissionCode[] value();
}

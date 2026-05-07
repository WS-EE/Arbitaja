package com.arbitaja.refactored.backend.scoring.adapter.in.web.annotations;

import com.arbitaja.refactored.backend.scoring.core.domain.enums.ScoringPermissionCode;
import lombok.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresScoringPermission {
    @NonNull ScoringPermissionCode[] value();
}

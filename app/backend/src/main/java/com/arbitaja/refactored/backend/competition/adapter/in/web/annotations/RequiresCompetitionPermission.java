package com.arbitaja.refactored.backend.competition.adapter.in.web.annotations;

import com.arbitaja.refactored.backend.competition.core.domain.enums.CompetitionPermissionCode;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresCompetitionPermission {
    @NotNull CompetitionPermissionCode[] value();
}


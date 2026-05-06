package com.arbitaja.refactored.backend.competition.adapter.in.web.annotations;

import com.arbitaja.refactored.backend.competition.core.domain.enums.CompetitionPermissionCode;
import lombok.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresCompetitionPermission {
    @NonNull CompetitionPermissionCode[] value();
}


package com.arbitaja.refactored.backend.competition.core.port.in.security;

import com.arbitaja.refactored.backend.competition.core.domain.enums.CompetitionPermissionCode;
import lombok.NonNull;

/**
 * Input port for permission checks in competition management.
 */
public interface CheckCompetitionPermissionUseCase {

    boolean assertUserHasPermissions(@NonNull String username, @NonNull CompetitionPermissionCode[] permissions);
}


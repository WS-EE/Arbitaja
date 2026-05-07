package com.arbitaja.refactored.backend.scoring.core.port.in.security;

import com.arbitaja.refactored.backend.scoring.core.domain.enums.ScoringPermissionCode;
import lombok.NonNull;

/**
 * Input port for permission checks in the scoring context.
 */
public interface CheckScoringPermissionUseCase {

    boolean assertUserHasPermissions(@NonNull String username, @NonNull ScoringPermissionCode[] permissions);

    boolean userIsAdmin(@NonNull String username);
}

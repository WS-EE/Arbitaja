package com.arbitaja.refactored.backend.scoring.core.port.out.security;

import com.arbitaja.refactored.backend.scoring.core.domain.enums.ScoringPermissionCode;
import lombok.NonNull;

/**
 * Output port for delegating scoring permission checks to external systems.
 */
public interface ScoringPermissionGatewayPort {

    boolean userHasPermissions(@NonNull String username, @NonNull ScoringPermissionCode[] permissions);

    boolean userIsAdmin(@NonNull String username);
}

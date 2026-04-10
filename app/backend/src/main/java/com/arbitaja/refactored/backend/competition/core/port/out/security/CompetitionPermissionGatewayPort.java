package com.arbitaja.refactored.backend.competition.core.port.out.security;

import com.arbitaja.refactored.backend.competition.core.domain.enums.CompetitionPermissionCode;
import lombok.NonNull;

/**
 * Output port for delegating competition permission checks to external systems.
 */
public interface CompetitionPermissionGatewayPort {

    boolean userHasPermissions(@NonNull String username, @NonNull CompetitionPermissionCode[] permissions);
}


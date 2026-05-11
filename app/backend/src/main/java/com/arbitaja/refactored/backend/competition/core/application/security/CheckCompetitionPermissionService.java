package com.arbitaja.refactored.backend.competition.core.application.security;

import com.arbitaja.refactored.backend.competition.core.domain.enums.CompetitionPermissionCode;
import com.arbitaja.refactored.backend.competition.core.port.in.security.CheckCompetitionPermissionUseCase;
import com.arbitaja.refactored.backend.competition.core.port.out.security.CompetitionPermissionGatewayPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Application service for permission checks in the competition context.
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class CheckCompetitionPermissionService implements CheckCompetitionPermissionUseCase {

    private final CompetitionPermissionGatewayPort permissionGateway;

    @Override
    public boolean assertUserHasPermissions(@NonNull String username, @NonNull CompetitionPermissionCode[] permissions) {
        return permissionGateway.userHasPermissions(username, permissions);
    }
}


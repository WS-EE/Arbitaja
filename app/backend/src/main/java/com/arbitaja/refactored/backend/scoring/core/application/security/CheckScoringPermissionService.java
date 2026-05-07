package com.arbitaja.refactored.backend.scoring.core.application.security;

import com.arbitaja.refactored.backend.scoring.core.domain.enums.ScoringPermissionCode;
import com.arbitaja.refactored.backend.scoring.core.port.in.security.CheckScoringPermissionUseCase;
import com.arbitaja.refactored.backend.scoring.core.port.out.security.ScoringPermissionGatewayPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Application service for permission checks in the scoring context.
 */
@Service
@RequiredArgsConstructor
public class CheckScoringPermissionService implements CheckScoringPermissionUseCase {

    private final ScoringPermissionGatewayPort permissionGateway;

    @Override
    public boolean assertUserHasPermissions(@NonNull String username, @NonNull ScoringPermissionCode[] permissions) {
        return permissionGateway.userHasPermissions(username, permissions);
    }

    @Override
    public boolean userIsAdmin(@NonNull String username) {
        return permissionGateway.userIsAdmin(username);
    }
}

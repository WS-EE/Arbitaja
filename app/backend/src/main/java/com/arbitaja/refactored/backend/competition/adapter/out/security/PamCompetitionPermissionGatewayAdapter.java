package com.arbitaja.refactored.backend.competition.adapter.out.security;

import com.arbitaja.refactored.backend.competition.core.domain.enums.CompetitionPermissionCode;
import com.arbitaja.refactored.backend.competition.core.port.out.security.CompetitionPermissionGatewayPort;
import com.arbitaja.refactored.backend.pam.core.domain.enums.PermissionCode;
import com.arbitaja.refactored.backend.pam.core.port.in.permission.CheckPermissionUseCase;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Outbound adapter delegating competition permission checks to PAM.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.competition.mode", havingValue = "hex")
public class PamCompetitionPermissionGatewayAdapter implements CompetitionPermissionGatewayPort {

    private final CheckPermissionUseCase checkPermissionUseCase;

    @Override
    public boolean userHasPermissions(@NonNull String username, @NonNull CompetitionPermissionCode[] permissions) {
        PermissionCode[] pamPermissions = Arrays.stream(permissions)
            .map(permission -> PermissionCode.valueOf(permission.name()))
            .toArray(PermissionCode[]::new);

        return checkPermissionUseCase.assertUserHasPermissions(username, pamPermissions);
    }
}


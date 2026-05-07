package com.arbitaja.refactored.backend.scoring.adapter.out.security;

import com.arbitaja.refactored.backend.pam.core.domain.enums.PermissionCode;
import com.arbitaja.refactored.backend.pam.core.port.in.permission.CheckPermissionUseCase;
import com.arbitaja.refactored.backend.scoring.core.domain.enums.ScoringPermissionCode;
import com.arbitaja.refactored.backend.scoring.core.port.out.security.ScoringPermissionGatewayPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Outbound adapter delegating scoring permission checks to PAM.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.scoring.mode", havingValue = "hex", matchIfMissing = true)
public class PamScoringPermissionGatewayAdapter implements ScoringPermissionGatewayPort {

    private final CheckPermissionUseCase checkPermissionUseCase;

    @Override
    public boolean userHasPermissions(@NonNull String username, @NonNull ScoringPermissionCode[] permissions) {
        PermissionCode[] pamPermissions = Arrays.stream(permissions)
            .map(permission -> PermissionCode.valueOf(permission.name()))
            .toArray(PermissionCode[]::new);
        return checkPermissionUseCase.assertUserHasPermissions(username, pamPermissions);
    }

    @Override
    public boolean userIsAdmin(@NonNull String username) {
        return checkPermissionUseCase.assertUserHasPermissions(username, new PermissionCode[]{PermissionCode.ADMIN});
    }
}

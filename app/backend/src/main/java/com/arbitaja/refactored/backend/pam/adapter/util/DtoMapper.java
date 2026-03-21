package com.arbitaja.refactored.backend.pam.adapter.util;

import com.arbitaja.refactored.backend.pam.adapter.in.web.dto.response.RoleResponse;
import com.arbitaja.refactored.backend.pam.adapter.in.web.dto.response.UserProfileResponse;
import com.arbitaja.refactored.backend.pam.core.domain.enums.PermissionCode;
import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import com.arbitaja.refactored.backend.pam.core.domain.model.RolePermission;
import com.arbitaja.refactored.backend.pam.core.domain.model.User;
import com.arbitaja.refactored.backend.pam.core.domain.model.UserRole;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
public class DtoMapper {

    public static UserProfileResponse toUserProfileResponse(User user) {
        if (user == null) {
            return null;
        }

        List<RoleResponse> roles = user.getUserRoles().stream()
                .map(UserRole::getRole)
                .map(DtoMapper::toRoleResponse)
                .toList();

        Set<PermissionCode> permissionCodes = roles.stream()
                .flatMap(role -> role.permissions().stream())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRoles(roles);
        response.setPermissions(permissionCodes);
        response.setPersonalData(user.getPersonalData() != null ? new UserProfileResponse.PersonalDataResponse(
                user.getPersonalData().getId(),
                user.getPersonalData().getFullName(),
                user.getPersonalData().getEmail(),
                user.getPersonalData().getSchool() != null
                    ? new UserProfileResponse.SchoolResponse(
                        user.getPersonalData().getSchool().getId(),
                        user.getPersonalData().getSchool().getName()
                ) : null
        ) : null);
        return response;
    }

    private static RoleResponse toRoleResponse(Role role) {
        List<PermissionCode> permissions = role.getRolePermissions().stream()
                .map(RolePermission::getPermission)
                .map(permission -> toPermissionCodeOrNull(permission.getKey()))
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        return new RoleResponse(
                role.getId(),
                role.getName(),
                permissions
        );
    }

    private static PermissionCode toPermissionCodeOrNull(String permissionKey) {
        try {
            return PermissionCode.valueOf(permissionKey);
        } catch (IllegalArgumentException e) {
            log.error("Unknown permission key: {}", permissionKey, e);
            return null;
        }
    }
}

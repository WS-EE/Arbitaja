package com.arbitaja.refactored.backend.pam.adapter.util;


import com.arbitaja.refactored.backend.pam.adapter.in.web.dto.UserProfileResponse;
import com.arbitaja.refactored.backend.pam.core.domain.model.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DtoMapper {

    public static UserProfileResponse toUserProfileResponse(User user) {
        if (user == null) {
            return null;
        }

        List<Role> roles = user.getUserRoles().stream()
                .map(UserRole::getRole)
                .toList();

        List <Permission> permissions = roles.stream().map(role -> role.getRolePermissions().stream().map(RolePermission::getPermission).toList()).toList().stream().flatMap(List::stream).distinct().toList();

        Set<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toSet());


        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRoles(roles);
        response.setPermissions(authorities);
        return response;
    }
}

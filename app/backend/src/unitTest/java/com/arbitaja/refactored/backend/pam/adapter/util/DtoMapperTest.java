package com.arbitaja.refactored.backend.pam.adapter.util;

import com.arbitaja.refactored.backend.pam.adapter.in.web.user.dto.response.UserProfileResponse;
import com.arbitaja.refactored.backend.pam.core.domain.enums.PermissionCode;
import com.arbitaja.refactored.backend.pam.core.domain.model.Permission;
import com.arbitaja.refactored.backend.pam.core.domain.model.PersonalData;
import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import com.arbitaja.refactored.backend.pam.core.domain.model.RolePermission;
import com.arbitaja.refactored.backend.pam.core.domain.model.School;
import com.arbitaja.refactored.backend.pam.core.domain.model.User;
import com.arbitaja.refactored.backend.pam.core.domain.model.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DtoMapperTest {

    @Test
    void toUserProfileResponseReturnsNullForNullUser() {
        assertNull(DtoMapper.toUserProfileResponse(null));
    }

    @Test
    void toUserProfileResponseMapsRolesPermissionsAndPersonalData() {
        User user = User.builder()
            .id(7)
            .username("alice")
            .saltedPassword("hash")
            .personalData(PersonalData.builder()
                .id(1)
                .fullName("Alice")
                .email("alice@example.com")
                .school(School.builder().id(3).name("Test School").build())
                .build())
            .build();

        Role role = Role.builder().id(10).name("manager").build();
        role.getRolePermissions().add(RolePermission.builder()
            .role(role)
            .permission(Permission.builder().id(2).name("View users").key("VIEW_USERS").build())
            .build());
        role.getRolePermissions().add(RolePermission.builder()
            .role(role)
            .permission(Permission.builder().id(3).name("Unknown").key("NOT_A_PERMISSION").build())
            .build());
        user.getUserRoles().add(UserRole.builder().user(user).role(role).build());

        UserProfileResponse response = DtoMapper.toUserProfileResponse(user);

        assertEquals(7, response.getId());
        assertEquals("alice", response.getUsername());
        assertEquals("Alice", response.getPersonalData().getFullName());
        assertEquals(3, response.getPersonalData().getSchool().getId());
        assertEquals(1, response.getRoles().size());
        assertTrue(response.getPermissions().contains(PermissionCode.VIEW_USERS));
        assertEquals(1, response.getPermissions().size());
    }
}


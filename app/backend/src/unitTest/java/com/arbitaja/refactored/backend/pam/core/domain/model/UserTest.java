package com.arbitaja.refactored.backend.pam.core.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserTest {

    @Test
    void createNewBuildsUserWithRequiredFields() {
        PersonalData personalData = PersonalData.builder()
            .id(5)
            .fullName("Alice")
            .email("alice@example.com")
            .build();

        User user = User.createNew("alice", "hash", personalData);

        assertEquals("alice", user.getUsername());
        assertEquals("hash", user.getSaltedPassword());
        assertEquals(personalData, user.getPersonalData());
        assertNotNull(user.getApiTokens());
        assertNotNull(user.getUserRoles());
    }

    @Test
    void addRoleInitializesRolesWhenSetIsNull() {
        User user = User.builder().username("alice").saltedPassword("hash").build();
        user.setUserRoles(null);
        UserRole userRole = UserRole.builder()
            .user(user)
            .role(Role.builder().name("admin").build())
            .build();

        user.addRole(userRole);

        assertNotNull(user.getUserRoles());
        assertEquals(1, user.getUserRoles().size());
    }

    @Test
    void removeRoleDoesNothingWhenSetIsNull() {
        User user = User.builder().username("alice").saltedPassword("hash").build();
        user.setUserRoles(null);
        UserRole userRole = UserRole.builder()
            .user(user)
            .role(Role.builder().name("admin").build())
            .build();

        user.removeRole(userRole);

        assertNull(user.getUserRoles());
    }
}


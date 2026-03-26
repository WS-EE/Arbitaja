package com.arbitaja.refactored.backend.pam.core.domain.model;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Domain entity representing a User in the system.
 * This is the core domain model - independent of persistence and infrastructure concerns.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"saltedPassword", "apiTokens"})
public class User {

    private Integer id;

    @NonNull
    private String username;

    @NonNull
    private String saltedPassword;

    private PersonalData personalData;

    private ApiToken defaultToken;

    @Builder.Default
    private Set<ApiToken> apiTokens = new HashSet<>();

    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();

    /**
     * Factory method to create a new User with required fields
     */
    public static User createNew(@NonNull String username, @NonNull String saltedPassword, PersonalData personalData) {
        return User.builder()
            .username(username)
            .saltedPassword(saltedPassword)
            .personalData(personalData)
            .build();
    }

    /**
     * Adds a role to the user
     */
    public void addRole(UserRole userRole) {
        if (userRoles == null) {
            userRoles = new HashSet<>();
        }
        userRoles.add(userRole);
    }

    /**
     * Removes a role from the user
     */
    public void removeRole(UserRole userRole) {
        if (userRoles != null) {
            userRoles.remove(userRole);
        }
    }
}

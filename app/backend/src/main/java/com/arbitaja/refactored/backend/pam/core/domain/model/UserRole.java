package com.arbitaja.refactored.backend.pam.core.domain.model;

import lombok.*;

import java.sql.Timestamp;

/**
 * Domain entity representing a User-Role association.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode(of = {"user", "role"})
@ToString(exclude = {"user"})
public class UserRole {

    private Integer id;

    @NonNull
    private User user;

    @NonNull
    private Role role;

    private Timestamp createdAt;

    /**
     * Factory method to create new UserRole
     */
    public static UserRole createNew(@NonNull User user, @NonNull Role role) {
        return UserRole.builder()
            .user(user)
            .role(role)
            .createdAt(new Timestamp(System.currentTimeMillis()))
            .build();
    }
}

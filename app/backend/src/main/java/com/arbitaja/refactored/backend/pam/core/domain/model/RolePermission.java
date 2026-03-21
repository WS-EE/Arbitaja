package com.arbitaja.refactored.backend.pam.core.domain.model;

import lombok.*;


/**
 * Domain entity representing the association between a Role and a Permission.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class RolePermission {

    private Integer id;

    @NonNull
    private Permission permission;

    @NonNull
    private Role role;

    /**
     * Factory method to create new RolePermission
     */
    public static RolePermission createNew(@NonNull Permission permission, @NonNull Role role) {
        return RolePermission.builder()
                .permission(permission)
                .role(role)
                .build();
    }
}


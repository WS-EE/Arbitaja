package com.arbitaja.refactored.backend.pam.core.domain.model;

import lombok.*;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * Domain entity representing a Role in the permission system.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"userRoles"})
public class Role {

    private Integer id;

    @NonNull
    private String name;

    private Timestamp createdAt;

    private Timestamp changedAt;

    @Builder.Default
    private Set<RoleRelation> parentRoleRelations = new HashSet<>();

    @Builder.Default
    private Set<RoleRelation> childRoleRelations = new HashSet<>();

    @Builder.Default
    private Set<RolePermission> rolePermissions = new HashSet<>();

    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();

    /**
     * Factory method to create new Role
     */
    public static Role createNew(@NonNull String name) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return Role.builder()
            .name(name)
            .createdAt(now)
            .changedAt(now)
            .build();
    }
}

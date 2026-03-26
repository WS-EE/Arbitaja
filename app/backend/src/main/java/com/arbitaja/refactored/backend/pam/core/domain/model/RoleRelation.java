package com.arbitaja.refactored.backend.pam.core.domain.model;

import lombok.*;

/**
 * Domain entity representing Role hierarchy relationship.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class RoleRelation {

    private Integer id;

    @NonNull
    private Role parentRole;

    @NonNull
    private Role childRole;

    /**
     * Factory method to create new RoleRelation
     */
    public static RoleRelation createNew(@NonNull Role parentRole, @NonNull Role childRole) {
        return RoleRelation.builder()
            .parentRole(parentRole)
            .childRole(childRole)
            .build();
    }
}


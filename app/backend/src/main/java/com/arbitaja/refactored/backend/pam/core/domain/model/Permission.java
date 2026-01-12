package com.arbitaja.refactored.backend.pam.core.domain.model;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Domain entity representing a Permission.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class Permission {

    private Integer id;

    @NonNull
    private String name;

    @NonNull
    private String key;

    private String keyObject;

    @Builder.Default
    private Set<RolePermission> rolePermissions = new HashSet<>();

    /**
     * Factory method to create new Permission
     */
    public static Permission createNew(@NonNull String name, @NonNull String key, String keyObject) {
        return Permission.builder()
                .name(name)
                .key(key)
                .keyObject(keyObject)
                .build();
    }
}

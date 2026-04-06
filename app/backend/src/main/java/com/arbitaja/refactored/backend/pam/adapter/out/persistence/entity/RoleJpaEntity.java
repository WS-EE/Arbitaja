package com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * JPA Entity for Role persistence.
 */
@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class RoleJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    @NonNull
    private String name;

    @Schema(hidden = true)
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Schema(hidden = true)
    @Column(name = "changed_at")
    private Timestamp changedAt;

    @Builder.Default
    @OneToMany(mappedBy = "parentRole", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<RoleRelationJpaEntity> parentRoleRelations = new LinkedHashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "childRole", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<RoleRelationJpaEntity> childRoleRelations = new LinkedHashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<RolePermissionJpaEntity> rolePermissions = new LinkedHashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserRoleJpaEntity> userRoles = new LinkedHashSet<>();
}


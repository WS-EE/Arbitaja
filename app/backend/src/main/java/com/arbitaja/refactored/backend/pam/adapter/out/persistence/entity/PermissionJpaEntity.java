package com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * JPA Entity for Permission persistence.
 */
@Entity
@Table(name = "permission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class PermissionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false)
    @NonNull
    private String name;

    @Column(name = "key", nullable = false)
    @NonNull
    private String key;

    @Column(name = "key_object")
    private String keyObject;

    @Builder.Default
    @OneToMany(mappedBy = "permission", orphanRemoval = true, cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private Set<RolePermissionJpaEntity> rolePermissions = new LinkedHashSet<>();
}


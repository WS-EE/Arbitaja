package com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.Map;

/**
 * JPA Entity for RolePermission persistence.
 */
@Entity
@Table(name = "role_permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class RolePermissionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "permission_id", nullable = false)
    @NonNull
    private PermissionJpaEntity permission;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id", nullable = false)
    @NonNull
    private RoleJpaEntity role;

    @Type(JsonBinaryType.class)
    @Column(name = "key_object_acl", columnDefinition = "jsonb")
    private Map<String, String> keyObjectAcl;
}


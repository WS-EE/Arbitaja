package com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA Entity for RoleRelation persistence.
 */
@Entity
@Table(name = "role_relation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class RoleRelationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "parent_role_id")
    @NonNull
    private RoleJpaEntity parentRole;

    @ManyToOne
    @JoinColumn(name = "child_role_id")
    @NonNull
    private RoleJpaEntity childRole;
}


package com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

/**
 * JPA Entity for UserRole persistence.
 */
@Entity
@Table(name = "user_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"user"})
public class UserRoleJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NonNull
    private UserJpaEntity user;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    @NonNull
    private RoleJpaEntity role;

    @Column(name = "created_at")
    private Timestamp createdAt;
}


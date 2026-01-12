package com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA Entity for ApiToken persistence.
 */
@Entity
@Table(name = "api_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"token"})
public class ApiTokenJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NonNull
    private UserJpaEntity user;

    @Column(name = "name")
    @NonNull
    private String name;

    @Column(name = "token")
    @NonNull
    private String token;
}


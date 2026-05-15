package com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * JPA Entity for User persistence.
 */
@Entity
@Table(name = "\"user\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"saltedPassword", "apiTokens"})
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "default_token_id")
    @Schema(hidden = true)
    private ApiTokenJpaEntity defaultToken;

    @Column(name = "username", nullable = false)
    @NonNull
    private String username;

    @Column(name = "salted_password", nullable = false)
    @NonNull
    private String saltedPassword;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "personal_data_id")
    private PersonalDataJpaEntity personalData;

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<ApiTokenJpaEntity> apiTokens = new LinkedHashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.PERSIST)
    private Set<UserRoleJpaEntity> userRoles = new LinkedHashSet<>();
}


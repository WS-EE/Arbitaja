package com.arbitaja.refactored.backend.pam.core.domain.model;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Domain entity representing an API Token.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"token"})
public class ApiToken {

    private Integer id;

    @NonNull
    private User user;

    @NonNull
    private String name;

    @NonNull
    private String token;

    @Builder.Default
    private Set<Object> scoringAgents = new HashSet<>();

    /**
     * Factory method to create new ApiToken
     */
    public static ApiToken createNew(@NonNull User user, @NonNull String name, @NonNull String token) {
        return ApiToken.builder()
            .user(user)
            .name(name)
            .token(token)
            .build();
    }
}


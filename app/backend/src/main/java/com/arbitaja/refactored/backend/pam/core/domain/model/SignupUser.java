package com.arbitaja.refactored.backend.pam.core.domain.model;

import lombok.*;

import java.time.Instant;

/**
 * Domain entity representing a user pending signup approval.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"saltedPassword"})
public class SignupUser {

    private Integer id;

    @NonNull
    private String username;

    @NonNull
    private String saltedPassword;

    @NonNull
    private PersonalData personalData;

    @Builder.Default
    private Boolean isApproved = false;

    @Builder.Default
    private Instant createdAt = Instant.now();

    /**
     * Factory method to create new SignupUser
     */
    public static SignupUser createNew(@NonNull String username, @NonNull String saltedPassword, @NonNull PersonalData personalData) {
        return SignupUser.builder()
            .username(username)
            .saltedPassword(saltedPassword)
            .personalData(personalData)
            .isApproved(false)
            .createdAt(Instant.now())
            .build();
    }
}


package com.arbitaja.refactored.backend.pam.core.domain.model;

import lombok.*;

import java.sql.Timestamp;

/**
 * Domain entity representing personal data of a user or competitor.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class PersonalData {

    private Integer id;

    @NonNull
    private String fullName;

    @NonNull
    private String email;

    private School school;

    private Timestamp createdAt;

    /**
     * Factory method to create new PersonalData
     */
    public static PersonalData createNew(@NonNull String fullName, @NonNull String email, School school) {
        return PersonalData.builder()
            .fullName(fullName)
            .email(email)
            .school(school)
            .createdAt(new Timestamp(System.currentTimeMillis()))
            .build();
    }
}


package com.arbitaja.refactored.backend.pam.core.domain.model;

import lombok.*;

/**
 * Domain entity representing a School.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class School {

    private Integer id;

    @NonNull
    private String name;

    /**
     * Factory method to create new School
     */
    public static School createNew(@NonNull String name) {
        return School.builder()
            .name(name)
            .build();
    }
}

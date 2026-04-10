package com.arbitaja.refactored.backend.competition.core.domain.model;

import lombok.*;

/**
 * Minimal personal-data projection for competitor management.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class CompetitorPersonalData {

    private Integer id;
    private String fullName;
    private String email;
    private Integer schoolId;
}


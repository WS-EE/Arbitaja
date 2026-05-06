package com.arbitaja.refactored.backend.competition.core.domain.model;

import lombok.*;

/**
 * Organizer projection used by the competition domain.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class CompetitionOrganizer {

    private Integer id;
    private String fullName;
    private String username;
}


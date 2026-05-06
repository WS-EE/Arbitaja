package com.arbitaja.refactored.backend.competition.core.domain.model;

import lombok.*;

/**
 * Competitor projection for read-side competition views.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class CompetitionCompetitor {

    private Integer id;
    private String fullName;
    private String alias;
    private Integer publicDisplayNameType;
    private CompetitorPersonalData personalData;
}


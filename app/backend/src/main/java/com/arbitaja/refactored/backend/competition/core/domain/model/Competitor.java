package com.arbitaja.refactored.backend.competition.core.domain.model;

import lombok.*;

/**
 * Core domain model for competitor management.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Competitor {

    private Integer id;
    private String alias;
    private Integer publicDisplayNameType;
    private CompetitorPersonalData personalData;
}


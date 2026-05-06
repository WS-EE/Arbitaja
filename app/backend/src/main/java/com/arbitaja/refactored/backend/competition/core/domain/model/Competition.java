package com.arbitaja.refactored.backend.competition.core.domain.model;

import lombok.*;

import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Core domain model for competition management.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Competition {

    private Integer id;
    private String name;
    private Timestamp startTime;
    private Timestamp endTime;
    private Timestamp scoreShowtime;
    private Boolean publishScores;
    private CompetitionOrganizer organizer;

    @Builder.Default
    private Set<CompetitionCompetitor> competitors = new LinkedHashSet<>();
}


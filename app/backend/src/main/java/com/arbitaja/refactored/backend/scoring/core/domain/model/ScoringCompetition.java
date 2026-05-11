package com.arbitaja.refactored.backend.scoring.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * Slim competition projection used by scoring use cases.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ScoringCompetition {

    private Integer id;
    private String name;
    private Timestamp startTime;
    private Timestamp endTime;
    private Timestamp scoreShowtime;
    private Boolean publishScores;

    public boolean isActiveAt(Instant moment) {
        if (startTime == null || endTime == null) {
            return false;
        }
        Instant start = startTime.toInstant();
        Instant end = endTime.toInstant();
        return !moment.isBefore(start) && !moment.isAfter(end);
    }

    public boolean isScorePublished() {
        return Boolean.TRUE.equals(publishScores);
    }
}

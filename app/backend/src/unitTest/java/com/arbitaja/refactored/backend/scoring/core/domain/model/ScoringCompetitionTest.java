package com.arbitaja.refactored.backend.scoring.core.domain.model;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScoringCompetitionTest {

    @Test
    void isActiveAtReturnsTrueWhenMomentIsBetweenStartAndEnd() {
        Instant start = Instant.parse("2026-04-06T09:00:00Z");
        Instant end = Instant.parse("2026-04-06T18:00:00Z");
        ScoringCompetition competition = ScoringCompetition.builder()
            .startTime(Timestamp.from(start))
            .endTime(Timestamp.from(end))
            .build();

        assertTrue(competition.isActiveAt(Instant.parse("2026-04-06T12:00:00Z")));
    }

    @Test
    void isActiveAtReturnsFalseBeforeStart() {
        ScoringCompetition competition = ScoringCompetition.builder()
            .startTime(Timestamp.from(Instant.parse("2026-04-06T09:00:00Z")))
            .endTime(Timestamp.from(Instant.parse("2026-04-06T18:00:00Z")))
            .build();

        assertFalse(competition.isActiveAt(Instant.parse("2026-04-06T08:59:59Z")));
    }

    @Test
    void isActiveAtReturnsFalseAfterEnd() {
        ScoringCompetition competition = ScoringCompetition.builder()
            .startTime(Timestamp.from(Instant.parse("2026-04-06T09:00:00Z")))
            .endTime(Timestamp.from(Instant.parse("2026-04-06T18:00:00Z")))
            .build();

        assertFalse(competition.isActiveAt(Instant.parse("2026-04-06T18:00:01Z")));
    }

    @Test
    void isActiveAtReturnsFalseWhenTimesAreNull() {
        ScoringCompetition competition = ScoringCompetition.builder().build();

        assertFalse(competition.isActiveAt(Instant.now()));
    }

    @Test
    void isScorePublishedReflectsPublishScoresFlag() {
        assertTrue(ScoringCompetition.builder().publishScores(true).build().isScorePublished());
        assertFalse(ScoringCompetition.builder().publishScores(false).build().isScorePublished());
        assertFalse(ScoringCompetition.builder().publishScores(null).build().isScorePublished());
    }
}

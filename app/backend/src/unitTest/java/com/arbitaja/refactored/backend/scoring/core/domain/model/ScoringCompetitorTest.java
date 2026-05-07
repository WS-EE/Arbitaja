package com.arbitaja.refactored.backend.scoring.core.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScoringCompetitorTest {

    @Test
    void resolvedDisplayNameReturnsFullNameWhenTypeIsOne() {
        ScoringCompetitor competitor = ScoringCompetitor.builder()
            .alias("alias")
            .fullName("Full Name")
            .schoolName("Some School")
            .publicDisplayNameType(1)
            .build();

        assertEquals("Full Name", competitor.resolvedDisplayName());
    }

    @Test
    void resolvedDisplayNameReturnsSchoolWhenTypeIsTwo() {
        ScoringCompetitor competitor = ScoringCompetitor.builder()
            .alias("alias")
            .fullName("Full Name")
            .schoolName("Some School")
            .publicDisplayNameType(2)
            .build();

        assertEquals("Some School", competitor.resolvedDisplayName());
    }

    @Test
    void resolvedDisplayNameReturnsAliasWhenTypeIsThree() {
        ScoringCompetitor competitor = ScoringCompetitor.builder()
            .alias("alias")
            .fullName("Full Name")
            .schoolName("Some School")
            .publicDisplayNameType(3)
            .build();

        assertEquals("alias", competitor.resolvedDisplayName());
    }

    @Test
    void resolvedDisplayNameThrowsForUnknownType() {
        ScoringCompetitor competitor = ScoringCompetitor.builder()
            .publicDisplayNameType(99)
            .build();

        assertThrows(IllegalStateException.class, competitor::resolvedDisplayName);
    }

    @Test
    void resolvedDisplayNameThrowsWhenTypeIsNull() {
        ScoringCompetitor competitor = ScoringCompetitor.builder()
            .publicDisplayNameType(null)
            .build();

        assertThrows(IllegalStateException.class, competitor::resolvedDisplayName);
    }
}

package com.arbitaja.refactored.backend.scoring.adapter.out.persistence.projection;

/**
 * Slim projection of the {@code competitor} table joined with personal data and school
 * needed for scoring display-name resolution.
 */
public interface ScoringCompetitorProjection {

    Integer getId();

    String getAlias();

    Integer getPublicDisplayNameType();

    String getFullName();

    String getSchoolName();
}

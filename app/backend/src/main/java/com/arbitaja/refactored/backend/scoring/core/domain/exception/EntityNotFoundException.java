package com.arbitaja.refactored.backend.scoring.core.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when an expected scoring-related entity is not found.
 */
public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public static EntityNotFoundException competition(Integer id) {
        return new EntityNotFoundException("Competition not found with id: " + id);
    }

    public static EntityNotFoundException competitor(Integer id) {
        return new EntityNotFoundException("Competitor not found with id: " + id);
    }

    public static EntityNotFoundException scoringCriterion(Integer id) {
        return new EntityNotFoundException("Scoring criterion not found with id: " + id);
    }

    public static EntityNotFoundException competitorInCompetition(Integer competitionId, Integer competitorId) {
        return new EntityNotFoundException(
            "Competitor " + competitorId + " is not assigned to competition " + competitionId
        );
    }
}

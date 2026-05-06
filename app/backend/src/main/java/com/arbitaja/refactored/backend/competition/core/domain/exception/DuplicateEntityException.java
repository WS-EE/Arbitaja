package com.arbitaja.refactored.backend.competition.core.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when an entity uniqueness constraint is violated.
 */
public class DuplicateEntityException extends DomainException {

    public DuplicateEntityException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public static DuplicateEntityException competitionByName(String name) {
        return new DuplicateEntityException("Competition with name already exists: " + name);
    }

    public static DuplicateEntityException competitorByAlias(String alias) {
        return new DuplicateEntityException("Competitor with alias already exists: " + alias);
    }

    public static DuplicateEntityException schoolByName(String name) {
        return new DuplicateEntityException("School with name already exists: " + name);
    }

    public static DuplicateEntityException competitorAlreadyInCompetition(Integer competitionId, Integer competitorId) {
        return new DuplicateEntityException(
            "Competitor " + competitorId + " is already assigned to competition " + competitionId
        );
    }
}


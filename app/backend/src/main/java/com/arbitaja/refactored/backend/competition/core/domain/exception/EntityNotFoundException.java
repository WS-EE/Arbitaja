package com.arbitaja.refactored.backend.competition.core.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when an expected entity is not found.
 */
public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public static EntityNotFoundException competition(Integer id) {
        return new EntityNotFoundException("Competition not found with id: " + id);
    }

    public static EntityNotFoundException competitionByName(String name) {
        return new EntityNotFoundException("Competition not found with name: " + name);
    }

    public static EntityNotFoundException organizer(Integer id) {
        return new EntityNotFoundException("Organizer not found with id: " + id);
    }

    public static EntityNotFoundException competitor(Integer id) {
        return new EntityNotFoundException("Competitor not found with id: " + id);
    }

    public static EntityNotFoundException personalData(Integer id) {
        return new EntityNotFoundException("Personal data not found with id: " + id);
    }

    public static EntityNotFoundException school(Integer id) {
        return new EntityNotFoundException("School not found with id: " + id);
    }

    public static EntityNotFoundException competitorInCompetition(Integer competitionId, Integer competitorId) {
        return new EntityNotFoundException(
            "Competitor " + competitorId + " is not assigned to competition " + competitionId
        );
    }
}


package com.arbitaja.refactored.backend.scoring.core.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when input fails domain validation rules.
 */
public class ValidationException extends DomainException {

    public ValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public static ValidationException pointsOutOfRange(Double maxPoints) {
        return new ValidationException("Points given have to be between 0 and " + maxPoints);
    }

    public static ValidationException competitionNotActive() {
        return new ValidationException("Competition is not active");
    }
}

package com.arbitaja.refactored.backend.scoring.core.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when access to scoring resources is denied.
 */
public class ForbiddenException extends DomainException {

    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public static ForbiddenException scoresNotPublished() {
        return new ForbiddenException("Scores are not published yet");
    }
}

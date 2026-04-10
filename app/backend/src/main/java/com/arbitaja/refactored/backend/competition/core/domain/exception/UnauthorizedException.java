package com.arbitaja.refactored.backend.competition.core.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when the caller does not have required permissions.
 */
public class UnauthorizedException extends DomainException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}


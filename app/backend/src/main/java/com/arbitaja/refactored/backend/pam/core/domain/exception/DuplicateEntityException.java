package com.arbitaja.refactored.backend.pam.core.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a duplicate entity is found.
 */
public class DuplicateEntityException extends DomainException {

    public DuplicateEntityException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public static DuplicateEntityException userWithUsername(String username) {
        return new DuplicateEntityException("User with username already exists: " + username);
    }
}


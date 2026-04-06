package com.arbitaja.refactored.backend.pam.core.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when user is not authorized to perform an action.
 */
public class UnauthorizedException extends DomainException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public static UnauthorizedException notAuthorizedToModifyUser() {
        return new UnauthorizedException("User not authorized to change other user");
    }
}


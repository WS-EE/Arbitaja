package com.arbitaja.refactored.backend.pam.core.domain.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends DomainException {

    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public static ForbiddenException notAuthorizedToModifyUser(){
        return new ForbiddenException("User not authorized to change other user");
    }
}

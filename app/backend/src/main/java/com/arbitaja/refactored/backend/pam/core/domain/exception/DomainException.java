package com.arbitaja.refactored.backend.pam.core.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base domain exception for the PAM module.
 */
@Getter
public abstract class DomainException extends RuntimeException {

    private final HttpStatus status;

    protected DomainException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}


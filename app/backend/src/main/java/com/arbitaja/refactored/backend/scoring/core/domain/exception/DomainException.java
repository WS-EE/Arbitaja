package com.arbitaja.refactored.backend.scoring.core.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception type for scoring management domain failures.
 */
@Getter
public class DomainException extends RuntimeException {

    private final HttpStatus status;

    public DomainException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}

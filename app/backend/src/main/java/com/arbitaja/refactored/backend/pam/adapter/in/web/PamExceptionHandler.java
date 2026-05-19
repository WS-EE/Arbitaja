package com.arbitaja.refactored.backend.pam.adapter.in.web;

import com.arbitaja.refactored.backend.pam.core.domain.exception.DomainException;
import com.arbitaja.refactored.backend.pam.core.domain.exception.DuplicateEntityException;
import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.exception.ForbiddenException;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Exception handler for the PAM module.
 * Translates domain exceptions into HTTP responses.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "com.arbitaja.refactored.backend.pam")
@Log4j2
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class PamExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("Entity not found: ", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of(
                "error", "Object not found",
                "message", ex.getMessage()
            ));
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateEntityException(DuplicateEntityException ex) {
        log.error("Duplicate entity: ", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Map.of(
                "error", "Duplicate entry",
                "message", ex.getMessage()
            ));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, String>> handleForbiddenException(ForbiddenException ex) {
        log.error("Forbidden: ", ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(Map.of(
                "error", "Forbidden",
                "message", ex.getMessage()
            ));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, String>> handleDomainException(DomainException ex) {
        log.error("Domain exception: ", ex);
        return ResponseEntity.status(ex.getStatus())
            .body(Map.of(
                "error", "Domain error",
                "message", ex.getMessage()
            ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Invalid input: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                "error", "Invalid input",
                "message", ex.getMessage()
            ));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, String>> handleNullPointerException(NullPointerException ex) {
        log.error("Null pointer exception: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                "error", "Required field is null",
                "message", ex.getMessage() != null ? ex.getMessage() : "A required field was null"
            ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "Internal server error",
                        "message", "An unexpected error occurred"
                ));
    }
}


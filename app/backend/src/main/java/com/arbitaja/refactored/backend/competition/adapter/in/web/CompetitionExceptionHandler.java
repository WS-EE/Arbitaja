package com.arbitaja.refactored.backend.competition.adapter.in.web;

import com.arbitaja.refactored.backend.competition.core.domain.exception.DomainException;
import com.arbitaja.refactored.backend.competition.core.domain.exception.DuplicateEntityException;
import com.arbitaja.refactored.backend.competition.core.domain.exception.EntityNotFoundException;
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
 * Exception mapper for competition management APIs.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "com.arbitaja.refactored.backend.competition")
@Log4j2
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class CompetitionExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "Object not found", "message", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateEntityException(DuplicateEntityException ex) {
        log.warn("Duplicate entity: {}", ex.toString());
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Map.of("error", "Duplicate entry", "message", ex.getMessage()));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, String>> handleDomainException(DomainException ex) {
        log.error("Domain exception: {}", ex.toString());
        return ResponseEntity.status(ex.getStatus())
            .body(Map.of("error", "Domain error", "message", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Invalid input: {}", ex.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of("error", "Invalid input", "message", ex.getMessage()));
    }
}


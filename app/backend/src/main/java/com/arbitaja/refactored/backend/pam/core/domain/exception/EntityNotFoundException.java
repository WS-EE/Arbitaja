package com.arbitaja.refactored.backend.pam.core.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested entity is not found.
 */
public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public static EntityNotFoundException user(Integer id) {
        return new EntityNotFoundException("User not found with id: " + id);
    }

    public static EntityNotFoundException userByUsername(String username) {
        return new EntityNotFoundException("User not found with username: " + username);
    }

    public static EntityNotFoundException role(Integer id) {
        return new EntityNotFoundException("Role not found with id: " + id);
    }

    public static EntityNotFoundException roleByName(String name) {
        return new EntityNotFoundException("Role not found with name: " + name);
    }

    public static EntityNotFoundException permission(Integer id) {
        return new EntityNotFoundException("Permission not found with id: " + id);
    }

    public static EntityNotFoundException school(Integer id) {
        return new EntityNotFoundException("School not found with id: " + id);
    }

    public static EntityNotFoundException signupUser(Integer id) {
        return new EntityNotFoundException("SignupUser not found with id: " + id);
    }
}


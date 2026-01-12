package com.arbitaja.refactored.backend.pam.core.port.out.security;

import lombok.NonNull;

/**
 * Output port for password encoding operations.
 * Abstracts the password encoding mechanism from the domain.
 */
public interface PasswordEncoderPort {

    /**
     * Encode a raw password
     * @param rawPassword The raw password to encode
     * @return Encoded password
     */
    String encode(@NonNull String rawPassword);

    /**
     * Check if a raw password matches an encoded password
     * @param rawPassword The raw password to check
     * @param encodedPassword The encoded password to match against
     * @return true if passwords match
     */
    boolean matches(@NonNull String rawPassword, @NonNull String encodedPassword);
}


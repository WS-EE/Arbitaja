package com.arbitaja.refactored.backend.pam.adapter.out.security;

import com.arbitaja.refactored.backend.pam.core.port.out.security.PasswordEncoderPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Security adapter implementing PasswordEncoderPort.
 * Delegates to Spring Security's PasswordEncoder.
 */
@Component
@RequiredArgsConstructor
public class PasswordEncoderAdapter implements PasswordEncoderPort {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String encode(@NonNull String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(@NonNull String rawPassword, @NonNull String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}


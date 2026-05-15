package com.arbitaja.refactored.backend.pam.adapter.in.web.signup.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupResponse {

    @NotBlank @NotNull Long userId;
    @NotBlank @NotNull String username;
    String email;
    String fullName;
    Integer schoolId;
    Instant createdAt;
}


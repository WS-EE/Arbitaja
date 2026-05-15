package com.arbitaja.refactored.backend.pam.adapter.in.web.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ChangePasswordRequest(

    String oldPassword,
    @NotNull @NotEmpty String newPassword
) {
}

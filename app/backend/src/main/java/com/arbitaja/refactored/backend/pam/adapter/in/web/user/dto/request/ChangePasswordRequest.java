package com.arbitaja.refactored.backend.pam.adapter.in.web.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.NonNull;

public record ChangePasswordRequest(

    String oldPassword,
    @NonNull @NotEmpty String newPassword
) {
}

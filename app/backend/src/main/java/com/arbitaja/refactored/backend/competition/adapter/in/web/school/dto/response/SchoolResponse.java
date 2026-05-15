package com.arbitaja.refactored.backend.competition.adapter.in.web.school.dto.response;

import jakarta.validation.constraints.NotNull;

public record SchoolResponse(
    @NotNull
    Integer id,
    String name
) {
}


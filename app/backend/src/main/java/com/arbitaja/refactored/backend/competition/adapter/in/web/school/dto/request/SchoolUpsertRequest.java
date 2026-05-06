package com.arbitaja.refactored.backend.competition.adapter.in.web.school.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SchoolUpsertRequest(
	@NotBlank String name
) {
}


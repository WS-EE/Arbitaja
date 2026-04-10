package com.arbitaja.refactored.backend.competition.adapter.util;

import com.arbitaja.refactored.backend.competition.adapter.in.web.school.dto.request.SchoolUpsertRequest;
import com.arbitaja.refactored.backend.competition.adapter.in.web.school.dto.response.SchoolResponse;
import com.arbitaja.refactored.backend.competition.core.domain.model.School;
import com.arbitaja.refactored.backend.competition.core.port.in.school.ManageSchoolUseCase;
import org.springframework.stereotype.Component;

@Component
public class SchoolWebMapper {

    public ManageSchoolUseCase.UpsertSchoolCommand toCommand(SchoolUpsertRequest request) {
        return ManageSchoolUseCase.UpsertSchoolCommand.builder()
            .name(request.name())
            .build();
    }

    public SchoolResponse toResponse(School school) {
        return new SchoolResponse(school.getId(), school.getName());
    }
}


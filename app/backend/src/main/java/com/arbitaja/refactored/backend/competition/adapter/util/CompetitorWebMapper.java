package com.arbitaja.refactored.backend.competition.adapter.util;

import com.arbitaja.refactored.backend.competition.adapter.in.web.competitor.dto.request.CompetitorUpsertRequest;
import com.arbitaja.refactored.backend.competition.adapter.in.web.competitor.dto.response.CompetitorResponse;
import com.arbitaja.refactored.backend.competition.core.domain.model.Competitor;
import com.arbitaja.refactored.backend.competition.core.domain.model.CompetitorPersonalData;
import com.arbitaja.refactored.backend.competition.core.port.in.competitor.ManageCompetitorUseCase;
import org.springframework.stereotype.Component;

@Component
public class CompetitorWebMapper {

    public ManageCompetitorUseCase.UpsertCompetitorCommand toCommand(CompetitorUpsertRequest request) {
        return ManageCompetitorUseCase.UpsertCompetitorCommand.builder()
            .alias(request.alias())
            .publicDisplayNameType(request.publicDisplayNameType())
            .personalDataId(request.personalDataId())
            .fullName(request.fullName())
            .email(request.email())
            .schoolId(request.schoolId())
            .build();
    }

    public CompetitorResponse toResponse(Competitor competitor) {
        CompetitorResponse.PersonalDataResponse personalData = null;
        if (competitor.getPersonalData() != null) {
            CompetitorPersonalData p = competitor.getPersonalData();
            personalData = new CompetitorResponse.PersonalDataResponse(
                p.getId(),
                p.getFullName(),
                p.getEmail(),
                p.getSchoolId()
            );
        }

        return new CompetitorResponse(
            competitor.getId(),
            competitor.getAlias(),
            competitor.getPublicDisplayNameType(),
            personalData
        );
    }
}


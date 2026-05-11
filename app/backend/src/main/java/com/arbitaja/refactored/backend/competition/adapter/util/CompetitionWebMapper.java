package com.arbitaja.refactored.backend.competition.adapter.util;

import com.arbitaja.refactored.backend.competition.adapter.in.web.competition.dto.request.CompetitionUpsertRequest;
import com.arbitaja.refactored.backend.competition.adapter.in.web.competition.dto.response.CompetitionResponse;
import com.arbitaja.refactored.backend.competition.core.domain.model.Competition;
import com.arbitaja.refactored.backend.competition.core.port.in.competition.ManageCompetitionUseCase;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Maps web DTOs to use-case commands and domain models to API responses.
 */
@Component
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class CompetitionWebMapper {

    public ManageCompetitionUseCase.UpsertCompetitionCommand toCommand(CompetitionUpsertRequest request) {
        return ManageCompetitionUseCase.UpsertCompetitionCommand.builder()
            .name(request.name())
            .startTime(request.startTime())
            .endTime(request.endTime())
            .scoreShowtime(request.scoreShowtime())
            .publishScores(request.publishScores())
            .organizerId(request.organizerId())
            .build();
    }

    public CompetitionResponse toResponse(Competition competition) {
        CompetitionResponse.OrganizerResponse organizer = null;
        if (competition.getOrganizer() != null) {
            organizer = new CompetitionResponse.OrganizerResponse(
                competition.getOrganizer().getId(),
                competition.getOrganizer().getFullName(),
                competition.getOrganizer().getUsername()
            );
        }

        Set<CompetitionResponse.CompetitorResponse> competitors = competition.getCompetitors().stream()
            .map(c -> new CompetitionResponse.CompetitorResponse(c.getId(), c.getFullName(), c.getAlias()))
            .collect(Collectors.toSet());

        return new CompetitionResponse(
            competition.getId(),
            competition.getName(),
            competition.getStartTime(),
            competition.getEndTime(),
            competition.getScoreShowtime(),
            competition.getPublishScores(),
            organizer,
            competitors
        );
    }
}


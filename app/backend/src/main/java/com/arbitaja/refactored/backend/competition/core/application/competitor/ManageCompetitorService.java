package com.arbitaja.refactored.backend.competition.core.application.competitor;

import com.arbitaja.refactored.backend.competition.adapter.out.persistence.CompetitionMembershipPersistenceAdapter;
import com.arbitaja.refactored.backend.competition.core.domain.exception.DuplicateEntityException;
import com.arbitaja.refactored.backend.competition.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.competition.core.domain.model.Competitor;
import com.arbitaja.refactored.backend.competition.core.domain.model.CompetitorPersonalData;
import com.arbitaja.refactored.backend.competition.core.domain.model.School;
import com.arbitaja.refactored.backend.competition.core.port.in.competitor.ManageCompetitorUseCase;
import com.arbitaja.refactored.backend.competition.core.port.out.competition.CompetitionMembershipPort;
import com.arbitaja.refactored.backend.competition.core.port.out.competitor.CompetitorRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for competitor create/update flows.
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class ManageCompetitorService implements ManageCompetitorUseCase {

    private final CompetitorRepositoryPort competitorRepository;
    private final CompetitionMembershipPort competitionMembershipPort;

    @Override
    @Transactional
    public Competitor createCompetitor(@NonNull UpsertCompetitorCommand command) {
        if (competitorRepository.existsByAlias(command.getAlias())) {
            throw DuplicateEntityException.competitorByAlias(command.getAlias());
        }
        Competitor competitor = competitorRepository.save(toDomain(null, command));

        if (command.getCompetitionId() != null) {
            competitionMembershipPort.addCompetitorToCompetition(command.getCompetitionId(), competitor.getId());
        }

        return competitor;
    }

    @Override
    @Transactional
    public Competitor updateCompetitor(@NonNull Integer id, @NonNull UpsertCompetitorCommand command) {
        if (competitorRepository.findById(id).isEmpty()) {
            throw EntityNotFoundException.competitor(id);
        }
        if (competitorRepository.existsByAliasAndIdNot(command.getAlias(), id)) {
            throw DuplicateEntityException.competitorByAlias(command.getAlias());
        }
        return competitorRepository.save(toDomain(id, command));
    }

    private Competitor toDomain(Integer id, UpsertCompetitorCommand command) {
        return Competitor.builder()
            .id(id)
            .alias(command.getAlias())
            .publicDisplayNameType(command.getPublicDisplayNameType())
            .personalData(CompetitorPersonalData.builder()
                .id(command.getPersonalDataId())
                .fullName(command.getFullName())
                .email(command.getEmail())
                .school(School.builder()
                    .id(command.getSchoolId())
                    .build())
                .build())
            .build();
    }
}


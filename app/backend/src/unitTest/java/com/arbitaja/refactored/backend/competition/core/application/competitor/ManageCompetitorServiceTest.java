package com.arbitaja.refactored.backend.competition.core.application.competitor;

import com.arbitaja.refactored.backend.competition.core.domain.exception.DuplicateEntityException;
import com.arbitaja.refactored.backend.competition.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.competition.core.domain.model.Competitor;
import com.arbitaja.refactored.backend.competition.core.port.in.competitor.ManageCompetitorUseCase;
import com.arbitaja.refactored.backend.competition.core.port.out.competitor.CompetitorRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageCompetitorServiceTest {

    @Mock
    private CompetitorRepositoryPort competitorRepository;

    @InjectMocks
    private ManageCompetitorService service;

    @Test
    void createCompetitorCreatesNewCompetitor() {
        ManageCompetitorUseCase.UpsertCompetitorCommand command = ManageCompetitorUseCase.UpsertCompetitorCommand.builder()
            .alias("runner-1")
            .publicDisplayNameType(1)
            .fullName("Runner One")
            .email("runner1@example.com")
            .build();

        Competitor saved = Competitor.builder().id(4).alias("runner-1").publicDisplayNameType(1).build();

        when(competitorRepository.existsByAlias("runner-1")).thenReturn(false);
        when(competitorRepository.save(any(Competitor.class))).thenReturn(saved);

        Competitor result = service.createCompetitor(command);

        assertEquals(4, result.getId());
        assertEquals("runner-1", result.getAlias());
    }

    @Test
    void createCompetitorThrowsOnDuplicateAlias() {
        ManageCompetitorUseCase.UpsertCompetitorCommand command = ManageCompetitorUseCase.UpsertCompetitorCommand.builder()
            .alias("runner-dup")
            .publicDisplayNameType(1)
            .build();

        when(competitorRepository.existsByAlias("runner-dup")).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> service.createCompetitor(command));
    }

    @Test
    void updateCompetitorThrowsWhenCompetitorMissing() {
        ManageCompetitorUseCase.UpsertCompetitorCommand command = ManageCompetitorUseCase.UpsertCompetitorCommand.builder()
            .alias("runner-updated")
            .publicDisplayNameType(2)
            .build();

        when(competitorRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateCompetitor(10, command));
    }
}


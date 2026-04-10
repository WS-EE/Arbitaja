package com.arbitaja.refactored.backend.competition.core.application.competition;

import com.arbitaja.refactored.backend.competition.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.competition.core.domain.model.Competition;
import com.arbitaja.refactored.backend.competition.core.domain.model.CompetitionCompetitor;
import com.arbitaja.refactored.backend.competition.core.port.out.competition.CompetitionCompetitorQueryPort;
import com.arbitaja.refactored.backend.competition.core.port.out.competition.CompetitionRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCompetitionServiceTest {

    @Mock
    private CompetitionRepositoryPort competitionRepository;

    @Mock
    private CompetitionCompetitorQueryPort competitionCompetitorQuery;

    @InjectMocks
    private GetCompetitionService service;

    @Test
    void getCompetitionByIdReturnsCompetitionWithCompetitors() {
        Competition competition = Competition.builder()
            .id(5)
            .name("Final")
            .startTime(Timestamp.valueOf("2026-04-06 09:00:00"))
            .endTime(Timestamp.valueOf("2026-04-06 18:00:00"))
            .build();

        Set<CompetitionCompetitor> competitors = Set.of(
            CompetitionCompetitor.builder().id(1).fullName("Alice").alias("A").build()
        );

        when(competitionRepository.findById(5)).thenReturn(Optional.of(competition));
        when(competitionCompetitorQuery.findByCompetitionId(5)).thenReturn(competitors);

        Competition result = service.getCompetitionById(5);

        assertEquals("Final", result.getName());
        assertEquals(1, result.getCompetitors().size());
    }

    @Test
    void getAllCompetitionsEnrichesEachCompetition() {
        Competition c1 = Competition.builder().id(1).name("One").build();
        Competition c2 = Competition.builder().id(2).name("Two").build();

        when(competitionRepository.findAll()).thenReturn(List.of(c1, c2));
        when(competitionCompetitorQuery.findByCompetitionId(1)).thenReturn(Set.of());
        when(competitionCompetitorQuery.findByCompetitionId(2)).thenReturn(Set.of());

        List<Competition> result = service.getAllCompetitions();

        assertEquals(2, result.size());
    }

    @Test
    void getCompetitionByNameThrowsWhenMissing() {
        when(competitionRepository.findByName("missing")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getCompetitionByName("missing"));
    }
}


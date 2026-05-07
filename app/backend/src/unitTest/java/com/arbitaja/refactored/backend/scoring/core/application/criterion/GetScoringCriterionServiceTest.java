package com.arbitaja.refactored.backend.scoring.core.application.criterion;

import com.arbitaja.refactored.backend.scoring.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCriterion;
import com.arbitaja.refactored.backend.scoring.core.port.out.criterion.CompetitionScoringCriterionRepositoryPort;
import com.arbitaja.refactored.backend.scoring.core.port.out.criterion.ScoringCriterionRepositoryPort;
import com.arbitaja.refactored.backend.scoring.core.port.out.lookup.ScoringCompetitionLookupPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetScoringCriterionServiceTest {

    @Mock
    private ScoringCriterionRepositoryPort scoringCriterionRepository;

    @Mock
    private CompetitionScoringCriterionRepositoryPort competitionCriterionRepository;

    @Mock
    private ScoringCompetitionLookupPort competitionLookup;

    @InjectMocks
    private GetScoringCriterionService service;

    @Test
    void getAllScoringCriteriaReturnsAllFromRepository() {
        ScoringCriterion criterion = ScoringCriterion.builder().id(1).name("c1").build();
        when(scoringCriterionRepository.findAll()).thenReturn(List.of(criterion));

        List<ScoringCriterion> result = service.getAllScoringCriteria();

        assertEquals(1, result.size());
        assertEquals("c1", result.getFirst().getName());
    }

    @Test
    void getScoringCriterionByIdReturnsCriterionWhenFound() {
        ScoringCriterion criterion = ScoringCriterion.builder().id(7).name("speed").build();
        when(scoringCriterionRepository.findById(7)).thenReturn(Optional.of(criterion));

        ScoringCriterion result = service.getScoringCriterionById(7);

        assertEquals(7, result.getId());
        assertEquals("speed", result.getName());
    }

    @Test
    void getScoringCriterionByIdThrowsWhenMissing() {
        when(scoringCriterionRepository.findById(404)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getScoringCriterionById(404));
    }

    @Test
    void getScoringCriteriaForCompetitionThrowsWhenCompetitionMissing() {
        when(competitionLookup.existsById(99)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.getScoringCriteriaForCompetition(99));
    }

    @Test
    void getScoringCriteriaForCompetitionReturnsLinkedCriteria() {
        ScoringCriterion criterion = ScoringCriterion.builder().id(3).name("ssh").build();
        when(competitionLookup.existsById(1)).thenReturn(true);
        when(competitionCriterionRepository.findCriteriaForCompetition(1)).thenReturn(List.of(criterion));

        List<ScoringCriterion> result = service.getScoringCriteriaForCompetition(1);

        assertEquals(1, result.size());
        assertEquals("ssh", result.getFirst().getName());
    }
}

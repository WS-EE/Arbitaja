package com.arbitaja.backend.competitions.scorings.APIs;


import com.arbitaja.backend.GlobalExceptionHandler;
import com.arbitaja.backend.competitions.dataobjects.Competition;
import com.arbitaja.backend.competitions.repositories.CompetitionRepository;
import com.arbitaja.backend.competitions.scorings.APIs.Request.ScoringCriteriaAdd;
import com.arbitaja.backend.competitions.scorings.dataobjects.CompetitionScoringCriterion;
import com.arbitaja.backend.competitions.scorings.dataobjects.ScoringCriterion;
import com.arbitaja.backend.competitions.scorings.repositories.CompetitionScoringCriterionRepository;
import com.arbitaja.backend.competitions.scorings.repositories.ScoringCriterionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class ScoringCriterionService {


    private final ScoringCriterionRepository scoringCriterionRepository;
    private final CompetitionScoringCriterionRepository competitionScoringCriterionRepository;
    private final CompetitionRepository competitionRepository;
    private final GlobalExceptionHandler globalExceptionHandler;

    private ScoringCriterion getScoringCriterion(Integer criteria_id) {
        if(criteria_id == null || criteria_id < 0) {
            return null;
        }
        Optional<ScoringCriterion> scoringCriterion = scoringCriterionRepository.findById(criteria_id);
        if(scoringCriterion.isPresent()) {
            return scoringCriterion.get();
        }
        throw new IllegalArgumentException("Template ScoringCriterion not found");
    }

    public ScoringCriterionService(ScoringCriterionRepository scoringCriterionRepository, CompetitionScoringCriterionRepository competitionScoringCriterionRepository, CompetitionRepository competitionRepository, GlobalExceptionHandler globalExceptionHandler) {
        this.scoringCriterionRepository = scoringCriterionRepository;
        this.competitionScoringCriterionRepository = competitionScoringCriterionRepository;
        this.competitionRepository = competitionRepository;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    public ResponseEntity<?> addScoringCriteria(ScoringCriteriaAdd scoringCriterion) {
        if(scoringCriterion == null) {
            throw new IllegalArgumentException("Invalid input");
        }
        Optional<Competition> competition = Optional.empty();
        if(scoringCriterion.getCompetitionId() != null) {
            competition = competitionRepository.findById(scoringCriterion.getCompetitionId());
            if (competition.isEmpty()) {
                throw new IllegalArgumentException(String.valueOf(Map.of("error", "Invalid competition id")));
            }
        }
        ScoringCriterion newScoringCriterion = new ScoringCriterion(scoringCriterion);
        newScoringCriterion.setCriteriaTemplate(getScoringCriterion(scoringCriterion.getCriteriaTemplate()));
        scoringCriterionRepository.save(newScoringCriterion);
        if(scoringCriterion.getCompetitionId() != null) {
            competitionScoringCriterionRepository.save(new CompetitionScoringCriterion(competition.get(), newScoringCriterion));
        }
        return ResponseEntity.ok(newScoringCriterion);
    }

    public ResponseEntity<?> getScoringCriteria(Integer scoring_id) {
        if(scoring_id == null) {
            throw new IllegalArgumentException("Invalid input");
        }
        Optional<ScoringCriterion> scoringCriterion = scoringCriterionRepository.findById(scoring_id);
        if (scoringCriterion.isPresent()) {
            return ResponseEntity.ok(scoringCriterion.get());
        }
        return new ResponseEntity<>(Map.of("error","scoringCriterion not found"), HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> getAllScoringCriteria() {
       return ResponseEntity.ok(scoringCriterionRepository.findAll());
    }

    public ResponseEntity<?> findAllForCompetition(Integer competition_id) {
        return ResponseEntity.ok(competitionScoringCriterionRepository.findAllByCompetitionId(competition_id));
    }

    public ResponseEntity<?> deleteScoringCriteria(Integer scoring_id) {
        try {
            if (scoring_id == null) {
                throw new IllegalArgumentException("Invalid input");
            }
            Optional<ScoringCriterion> scoringCriterion = scoringCriterionRepository.findById(scoring_id);
            if (scoringCriterion.isPresent()) {
                scoringCriterionRepository.delete(scoringCriterion.get());
                return ResponseEntity.ok(Map.of("message", "scoring criteria deleted successfully"));
            }
            return new ResponseEntity<>(Map.of("error", "scoringCriterion not found"), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException ex) {
            return globalExceptionHandler.handleDataIntegrityViolationException(ex);
        }
    }

    public ResponseEntity<?> updateScoringCriteria(ScoringCriterion scoringCriterion) {
        if(scoringCriterion == null) {
            throw new IllegalArgumentException("Invalid input");
        }
        Optional<ScoringCriterion> existingScoringCriterion = scoringCriterionRepository.findById(scoringCriterion.getId());
        if (existingScoringCriterion.isPresent()) {
            scoringCriterion.setCriteriaTemplate(getScoringCriterion(scoringCriterion.getCriteriaTemplate().getId()));
            scoringCriterionRepository.save(scoringCriterion);
            return ResponseEntity.ok(scoringCriterion);
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<?> addScoringCriteriaToCompetition(Integer competition_id, Integer scoring_id) {
        if(competition_id == null || scoring_id == null) {
            throw new IllegalArgumentException("Invalid input");
        }
        Optional<ScoringCriterion> scoringCriterion = scoringCriterionRepository.findById(scoring_id);
        Optional<Competition> competition = competitionRepository.findById(competition_id);
        if (competition.isEmpty()) {
            return new ResponseEntity<>(Map.of("error","competition not found"), HttpStatus.NOT_FOUND);
        }
        if (scoringCriterion.isEmpty()) {
            return new ResponseEntity<>(Map.of("error","scoringCriterion not found"), HttpStatus.NOT_FOUND);
        }
        competitionScoringCriterionRepository.save(new CompetitionScoringCriterion(competition.get(), scoringCriterion.get()));
        return ResponseEntity.ok(Map.of("message","scoring criteria added to competition successfully"));
    }
}

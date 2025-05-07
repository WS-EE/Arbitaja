package com.arbitaja.backend.competitions.scorings.APIs;


import com.arbitaja.backend.GlobalExceptionHandler;
import com.arbitaja.backend.competitions.dataobjects.Competition;
import com.arbitaja.backend.competitions.repositories.CompetitionRepository;
import com.arbitaja.backend.competitions.scorings.APIs.Request.ScoringCriteriaAdd;
import com.arbitaja.backend.competitions.scorings.dataobjects.CompetitionScoringCriterion;
import com.arbitaja.backend.competitions.scorings.dataobjects.ScoringCriterion;
import com.arbitaja.backend.competitions.scorings.repositories.CompetitionScoringCriterionRepository;
import com.arbitaja.backend.competitions.scorings.repositories.ScoringCriterionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.Optional;

@Service
public class ScoringCriterionService {

    private static final Logger log = LogManager.getLogger(ScoringCriterionService.class);


    private final ScoringCriterionRepository scoringCriterionRepository;
    private final CompetitionScoringCriterionRepository competitionScoringCriterionRepository;
    private final CompetitionRepository competitionRepository;

    public ScoringCriterionService(ScoringCriterionRepository scoringCriterionRepository, CompetitionScoringCriterionRepository competitionScoringCriterionRepository, CompetitionRepository competitionRepository) {
        this.scoringCriterionRepository = scoringCriterionRepository;
        this.competitionScoringCriterionRepository = competitionScoringCriterionRepository;
        this.competitionRepository = competitionRepository;
    }

    /**
     * Fetches a scoring criterion by its ID.
     *
     * @param criteria_id the ID of the scoring criterion to fetch
     * @return the scoring criterion if found, null otherwise
     * @throws GlobalExceptionHandler.NotFoundException if the scoring criterion is not found
     */
    private ScoringCriterion getScoringCriterion(Integer criteria_id) throws GlobalExceptionHandler.NotFoundException {
        log.info("Fetching scoring criterion with ID: {}", criteria_id);
        // Check if ScoringCriterion exists
        Optional<ScoringCriterion> scoringCriterion = scoringCriterionRepository.findById(criteria_id);
        // If it exists, return it
        if(scoringCriterion.isPresent()) {
            return scoringCriterion.get();
        }
        // If it doesn't exist, throw an exception
        throw new GlobalExceptionHandler.NotFoundException("Template ScoringCriterion not found");
    }

    /**
     * Adds a new scoring criterion.
     *
     * @param scoringCriterion the scoring criterion to add
     * @return HTTP response with the added scoring criterion
     * @throws GlobalExceptionHandler.NotFoundException if the competition is not found
     */
    public ResponseEntity<?> addScoringCriteria(ScoringCriteriaAdd scoringCriterion) throws GlobalExceptionHandler.NotFoundException {
        // Validate input
        if(ObjectUtils.isEmpty(scoringCriterion)) {
            throw new IllegalArgumentException("Scoring criterion can not be null");
        }
        // Check if valid competition ID is provided
        Optional<Competition> competition = Optional.empty();
        if(scoringCriterion.getCompetitionId() != null) {
            // Check if competition exists
            competition = competitionRepository.findById(scoringCriterion.getCompetitionId());
            if (competition.isEmpty()) {
                throw new GlobalExceptionHandler.NotFoundException("Competition with id " + scoringCriterion.getCompetitionId() + " not found");
            }
        }
        // Create a new ScoringCriterion object
        ScoringCriterion newScoringCriterion = new ScoringCriterion(scoringCriterion);
        // Set the criteria template
        newScoringCriterion.setCriteriaTemplate(scoringCriterion.getCriteriaTemplate() == null ? null : getScoringCriterion(scoringCriterion.getCriteriaTemplate()));
        scoringCriterionRepository.save(newScoringCriterion);
        // If competition ID is provided, save the CompetitionScoringCriterion
        if(scoringCriterion.getCompetitionId() != null) {
            competitionScoringCriterionRepository.save(new CompetitionScoringCriterion(competition.get(), newScoringCriterion));
        }
        return ResponseEntity.ok(newScoringCriterion);
    }

    /**
     * Fetches a scoring criterion by its ID.
     *
     * @param scoring_id the ID of the scoring criterion to fetch
     * @return HTTP response with the scoring criterion if found
     * @throws GlobalExceptionHandler.NotFoundException if the scoring criterion is not found
     */
    public ResponseEntity<?> getScoringCriteria(Integer scoring_id) throws GlobalExceptionHandler.NotFoundException {
        // Find the scoring criterion by ID
        ScoringCriterion scoringCriterion = getScoringCriterion(scoring_id);
        return ResponseEntity.ok(scoringCriterion);

    }

    /**
     * Fetches all scoring criteria.
     *
     * @return HTTP response with all scoring criteria
     */
    public ResponseEntity<?> getAllScoringCriteria() {
       return ResponseEntity.ok(scoringCriterionRepository.findAll());
    }

    /**
     * Fetches all scoring criteria for a specific competition.
     *
     * @param competition_id the ID of the competition
     * @return HTTP response with all scoring criteria for the competition
     */
    public ResponseEntity<?> findAllForCompetition(Integer competition_id) {
        return ResponseEntity.ok(competitionScoringCriterionRepository.findAllByCompetitionId(competition_id));
    }

    /**
     * Deletes a scoring criterion by its ID.
     *
     * @param scoring_id the ID of the scoring criterion to delete
     * @return HTTP response indicating success or failure
     */
    public ResponseEntity<?> deleteScoringCriteria(Integer scoring_id) {
        ScoringCriterion scoringCriterion = getScoringCriterion(scoring_id);
        scoringCriterionRepository.delete(scoringCriterion);
        return ResponseEntity.ok(Map.of("message", "scoring criteria deleted successfully"));


    }

    /**
     * Updates an existing scoring criterion.
     *
     * @param scoringCriterion the scoring criterion to update
     * @return HTTP response with the updated scoring criterion
     * @throws GlobalExceptionHandler.NotFoundException if the scoring criterion is not found
     */
    public ResponseEntity<?> updateScoringCriteria(ScoringCriterion scoringCriterion) throws GlobalExceptionHandler.NotFoundException {
        // Check that criterion is not empty
        if(ObjectUtils.isEmpty(scoringCriterion)) {
            throw new IllegalArgumentException("Scoring criterion can not be null");
        }
        // Check that criterion with the given ID exists
        ScoringCriterion existingScoringCriterion = getScoringCriterion(scoringCriterion.getId());
        // Make the changes to existing criterion
        existingScoringCriterion.setDescription(scoringCriterion.getDescription());
        existingScoringCriterion.setName(scoringCriterion.getName());
        existingScoringCriterion.setIsManual(scoringCriterion.getIsManual());
        existingScoringCriterion.setTotalPoints(scoringCriterion.getTotalPoints());
        existingScoringCriterion.setIsGeneralized(scoringCriterion.getIsGeneralized());
        existingScoringCriterion.setScoringHost(scoringCriterion.getScoringHost() == null ? null : scoringCriterion.getScoringHost());
        existingScoringCriterion.setCriteriaTemplate(scoringCriterion.getCriteriaTemplate() == null ? null : getScoringCriterion(scoringCriterion.getCriteriaTemplate().getId()));
        scoringCriterionRepository.save(scoringCriterion);
        return ResponseEntity.ok(scoringCriterion);

    }

    /**
     * Adds a scoring criterion to a competition.
     *
     * @param competition_id the ID of the competition
     * @param scoring_id the ID of the scoring criterion
     * @return HTTP response indicating success or failure
     */
    public ResponseEntity<?> addScoringCriteriaToCompetition(Integer competition_id, Integer scoring_id) {
        // Validate input
        if(competition_id == null || scoring_id == null) {
            throw new IllegalArgumentException("Competition ID and Scoring ID cannot be null");
        }
        // Check if scoring criterion and competition exist
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

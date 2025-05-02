package com.arbitaja.backend.competitions.scorings.APIs;

import com.arbitaja.backend.competitions.scorings.APIs.Request.ScoringCriteriaAdd;
import com.arbitaja.backend.competitions.scorings.dataobjects.ScoringCriterion;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController("ScoringCriteria")
@RequestMapping("")
public class ScoringCriterionController {
    private static final Logger log = LogManager.getLogger(ScoringCriterionController.class);


    private final ScoringCriterionService scoringCriterionService;

    public ScoringCriterionController(ScoringCriterionService scoringCriterionService) {
        this.scoringCriterionService = scoringCriterionService;
    }

    @PostMapping("/scoring/criteria/add")
    @PreAuthorize("hasAuthority('admin')")
    @Transactional
    public ResponseEntity<?> addScoringCriteria(@RequestBody ScoringCriteriaAdd scoringCriterion) {
        ResponseEntity<?> resp = scoringCriterionService.addScoringCriteria(scoringCriterion);
        log.info("Sending Response for adding scoringCriteria: " + "{}", resp);
        return resp;
    }

    @GetMapping("/scoring/criteria/get")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> getScoringCriteria(@RequestParam Integer scoring_id) {
        ResponseEntity<?> resp = scoringCriterionService.getScoringCriteria(scoring_id);
        log.info("Sending Response for scoringCriteria: " + "{}", resp);
        return resp;
    }

    @GetMapping("/scoring/criteria/get/all")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> getAllScoringCriteria() {
        ResponseEntity<?> resp = scoringCriterionService.getAllScoringCriteria();
        log.info("Sending Response for all scoringCriteria: " + "{}", resp);
        return resp;
    }

    @GetMapping("/scoring/criteria/by/competition")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> findAllForCompetition(@RequestParam Integer competition_id) {
        ResponseEntity<?> resp = scoringCriterionService.findAllForCompetition(competition_id);
        log.info("Sending Response for all scoringCriteria for competition: " + "{}", resp);
        return resp;
    }

    @DeleteMapping("/scoring/criteria/delete")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> deleteScoringCriteria(@RequestParam Integer scoring_id) {
        ResponseEntity<?> resp = scoringCriterionService.deleteScoringCriteria(scoring_id);
        log.info("Sending Response for deleting scoringCriteria: " + "{}", resp);
        return resp;
    }

    @PutMapping("/scoring/criteria/update")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> updateScoringCriteria(@RequestBody ScoringCriterion scoringCriterion) {
        ResponseEntity<?> resp = scoringCriterionService.updateScoringCriteria(scoringCriterion);
        log.info("Sending Response for updating scoringCriteria: " + "{}", resp);
        return resp;
    }

    @PostMapping("/scoring/criteria/add/to/competition")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> addScoringCriteriaToCompetition(@RequestParam Integer competition_id, @RequestParam Integer scoring_id) {
        ResponseEntity<?> resp = scoringCriterionService.addScoringCriteriaToCompetition(competition_id, scoring_id);
        log.info("Sending Response for adding scoringCriteria to competition: " + "{}", resp);
        return resp;
    }
}

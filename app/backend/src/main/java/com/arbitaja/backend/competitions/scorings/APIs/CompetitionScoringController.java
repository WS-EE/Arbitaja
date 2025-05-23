package com.arbitaja.backend.competitions.scorings.APIs;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController("competitionScoring")
@RequestMapping("")
public class CompetitionScoringController {
    private static final Logger log = LogManager.getLogger(CompetitionScoringController.class);


    private final CompetitionScoringService competitionScoringService;

    public CompetitionScoringController(CompetitionScoringService competitionScoringService) {
        this.competitionScoringService = competitionScoringService;
    }

    @PostMapping("/competition/criteria/history/add")
    public ResponseEntity<?> addCompetitionScoringCriteriaHistory(@RequestParam Integer competition_id, @RequestParam Integer criteria_id, @RequestParam Integer competitor_id, @RequestParam Double points) {
        competitionScoringService.addCompetitionScoringCriteriaHistory(competition_id, criteria_id, competitor_id, points);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/dashboard/competition/history")
    public ResponseEntity<?> getCompetitionScoringHistory(@RequestParam Integer competition_id) {
        ResponseEntity<?> response = ResponseEntity.ok(competitionScoringService.getCompetitionScoringHistory(competition_id, SecurityContextHolder.getContext().getAuthentication()));
        log.info("Sending response for competition scoring history: {}", response);
        return response;
    }

    @GetMapping("/dashboard/competition/criteria")
    public ResponseEntity<?> getCompetitionScoringCriteria(@RequestParam Integer competition_id) {
        competitionScoringService.checkIfHasAccessToScoring(competition_id, SecurityContextHolder.getContext().getAuthentication());
        ResponseEntity<?> response = ResponseEntity.ok(competitionScoringService.getScoringCriteriaResultForCompetitors(competition_id));
        log.info("Sending response for competition scoring criteria: {}", response);
        return response;
    }

    @GetMapping("/dashboard/competition/criteria/competitor")
    public ResponseEntity<?> getCompetitionScoringCriteriaForCompetitor(@RequestParam Integer competition_id, @RequestParam Integer competitor_id) {
        log.info("Fetching scoring criteria for competitor with ID: {}", competitor_id);
        competitionScoringService.checkIfHasAccessToScoring(competition_id, SecurityContextHolder.getContext().getAuthentication());
        ResponseEntity<?> response = ResponseEntity.ok(competitionScoringService.getScoringCriteriaResultForCompetitor(competition_id, competitor_id));
        log.info("Sending response for competition scoring criteria for competitor: {}", response);
        return response;
    }
}

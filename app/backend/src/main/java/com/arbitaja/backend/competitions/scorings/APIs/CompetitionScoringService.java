package com.arbitaja.backend.competitions.scorings.APIs;


import com.arbitaja.backend.competitions.dataobjects.Competition;
import com.arbitaja.backend.competitions.repositories.CompetitionRepository;
import com.arbitaja.backend.competitions.scorings.APIs.Response.CompetitionScoringResponse;
import com.arbitaja.backend.competitions.scorings.dataobjects.ScoringCriterion;
import com.arbitaja.backend.competitions.scorings.dataobjects.ScoringHistory;
import com.arbitaja.backend.competitions.scorings.repositories.CompetitionScoringCriterionRepository;
import com.arbitaja.backend.competitions.scorings.repositories.ScoringCriterionRepository;
import com.arbitaja.backend.competitions.scorings.repositories.ScoringGroupsStructureRepository;
import com.arbitaja.backend.competitions.scorings.repositories.ScoringHistoryRepository;
import com.arbitaja.backend.competitors.dataobjects.Competitor;
import com.arbitaja.backend.competitors.repositories.CompetitorRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;


import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
public class CompetitionScoringService {

    private static final Logger log = LogManager.getLogger(CompetitionScoringService.class);


    @Autowired
    private CompetitionRepository competitionRepository;
    @Autowired
    private CompetitorRepository competitorRepository;
    @Autowired
    private ScoringGroupsStructureRepository scoringGroupsStructureRepository;
    @Autowired
    private ScoringCriterionRepository scoringCriterionRepository;
    @Autowired
    private ScoringHistoryRepository scoringHistoryRepository;
    @Autowired
    private CompetitionScoringCriterionRepository competitionScoringCriterionRepository;

    private Competition getCompetition(Integer competition_id) {
        if(competition_id == null) {
            throw new IllegalArgumentException("Invalid input");
        }
        Optional<Competition> competition = competitionRepository.findById(competition_id);
        if(competition.isPresent()) {
            return competition.get();
        }
        throw new IllegalArgumentException("Competition not found");
    }

    private ScoringCriterion getScoringCriterion(Integer criteria_id) {
        if(criteria_id == null) {
            throw new IllegalArgumentException("Invalid input");
        }
        Optional<ScoringCriterion> scoringCriterion = scoringCriterionRepository.findById(criteria_id);
        if(scoringCriterion.isPresent()) {
            return scoringCriterion.get();
        }
        throw new IllegalArgumentException("Scoring criterion not found");
    }

    private Competitor getCompetitor(Integer competitor_id) {
        if(competitor_id == null) {
            throw new IllegalArgumentException("Invalid input");
        }
        Optional<Competitor> competitor = competitorRepository.findById(competitor_id);
        if(competitor.isPresent()) {
            return competitor.get();
        }
        throw new IllegalArgumentException("Competitor not found");
    }



    public void addCompetitionScoringCriteriaHistory(Integer competition_id, Integer criteria_id, Integer competitor_id, Double points) {
        Competition competition = getCompetition(competition_id);
        ScoringCriterion scoringCriterion = getScoringCriterion(criteria_id);
        Competitor competitor = getCompetitor(competitor_id);

        ScoringHistory scoringHistory = new ScoringHistory(competitor, competition, scoringCriterion, points, Timestamp.from(Instant.now()));
        scoringHistoryRepository.save(scoringHistory);
    }


    public CompetitionScoringResponse getCompetitionScoringHistory(Integer competition_id, Authentication auth) {
        Competition competition = getCompetition(competition_id);
        Set<Competitor> competitors = competitorRepository.findByCompetitionId(competition_id);
        if(auth.getAuthorities().contains("ROLE_ADMIN")) {
            return setDashboard(competition, competitors);
        }
        return setDashboard(competition, competitors, competition.getScore_showtime());
    }


    private List<CompetitionScoringResponse.Result> setResults(Competition competition, Competitor competitor, Timestamp score_showtime) {
        Set<ScoringHistory> scoringHistories = scoringHistoryRepository.findByCompetitionIdAndCompetitorIdAndScoreShowtime(competition.getId(), competitor.getId(), score_showtime);
        Map<Integer, Double> criterionPoints = new HashMap<>();
        List<CompetitionScoringResponse.Result> results = new ArrayList<>();
        for(ScoringHistory scoringHistory : scoringHistories) {
            criterionPoints.put(scoringHistory.getScoringCriteria().getId(), scoringHistory.getPointsGiven());
            Double totalScore = 0.0;
            for(Map.Entry<Integer, Double> entry : criterionPoints.entrySet()) {
                totalScore += entry.getValue();
            }
            results.add(new CompetitionScoringResponse.Result(scoringHistory.getCreatedAt(), totalScore));
        }
        return results;
    }

    private Set<CompetitionScoringResponse.Competitor> setCompetitors(Competition competition, Set<Competitor> competitors, Timestamp score_showtime) {
        Set<CompetitionScoringResponse.Competitor> competitorSet = new HashSet<>();
        for (Competitor competitor1 : competitors) {
            List<CompetitionScoringResponse.Result> results = setResults(competition, competitor1, score_showtime);
            if (results.isEmpty()) competitorSet.add(new CompetitionScoringResponse.Competitor(competitor1.getAlias(), 0.0, results));
            else competitorSet.add(new CompetitionScoringResponse.Competitor(competitor1.getAlias(), results.getLast().getPoint_amount(), results));
        }
        return competitorSet;
    }

    private CompetitionScoringResponse setDashboard(Competition competition, Set<Competitor> competitors) {
        return setDashboard(competition, competitors, competition.getEnd_time());
    }

    private CompetitionScoringResponse setDashboard(Competition competition, Set<Competitor> competitors, Timestamp score_showtime) {
        return new CompetitionScoringResponse(new CompetitionScoringResponse.Dashboard(setCompetitors(competition, competitors, score_showtime)));
    }
}

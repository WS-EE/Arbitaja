package com.arbitaja.backend.competitions.scorings.APIs;


import com.arbitaja.backend.GlobalExceptionHandler;
import com.arbitaja.backend.competitions.dataobjects.Competition;
import com.arbitaja.backend.competitions.repositories.CompetitionRepository;
import com.arbitaja.backend.competitions.scorings.APIs.Response.CompetitionScoringResponse;
import com.arbitaja.backend.competitions.scorings.dataobjects.ScoringCriterion;
import com.arbitaja.backend.competitions.scorings.dataobjects.ScoringHistory;
import com.arbitaja.backend.competitions.scorings.repositories.ScoringCriterionRepository;
import com.arbitaja.backend.competitions.scorings.repositories.ScoringHistoryRepository;
import com.arbitaja.backend.competitors.dataobjects.Competitor;
import com.arbitaja.backend.competitors.repositories.CompetitorRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    private ScoringCriterionRepository scoringCriterionRepository;
    @Autowired
    private ScoringHistoryRepository scoringHistoryRepository;

    /**
     * Finds a competition by its Id.
     *
     * @param competition_id id of the competition being searched for
     * @return competition object if found
     */
    private Competition getCompetition(Integer competition_id) {
        log.info("Fetching competition with ID: " + competition_id);
        // Find the competition by its Id
        Optional<Competition> competition = competitionRepository.findById(competition_id);
        if(competition.isPresent()) {
            return competition.get();
        }
        // If not found, throw an exception
        throw new IllegalArgumentException("Competition not found");
    }

    /**
     * Finds ScoringCriterion by its id
     *
     * @param criteria_id Id of the ScoringCriterion being searched for
     * @return ScoringCriterion object if found
     */
    private ScoringCriterion getScoringCriterion(Integer criteria_id){
        log.info("Fetching scoring criterion with ID: " + criteria_id);
        // Find the scoring criterion by its Id
        Optional<ScoringCriterion> scoringCriterion = scoringCriterionRepository.findById(criteria_id);
        if(scoringCriterion.isPresent()) {
            return scoringCriterion.get();
        }
        // If not found, throw an exception
        throw new GlobalExceptionHandler.NotFoundException("Scoring criterion not found");
    }

    /**
     * Finds a competitor by its Id.
     *
     * @param competitor_id Id of the competitor being searched for
     * @return Competitor object if found
     */
    private Competitor getCompetitor(Integer competitor_id){
        log.info("Fetching competitor with ID: " + competitor_id);
        // Find the competitor by its Id
        Optional<Competitor> competitor = competitorRepository.findById(competitor_id);
        if(competitor.isPresent()) {
            return competitor.get();
        }
        // If not found, throw an exception
        throw new GlobalExceptionHandler.NotFoundException("Competitor not found");
    }


    /**
     * Adds a new scoring history entry for a competitor in a competition.
     *
     * @param competition_id competition the competitor is participating in
     * @param criteria_id scoring criterion used for the competition
     * @param competitor_id competitor who is being scored
     * @param points points given to the competitor
     */
    public void addCompetitionScoringCriteriaHistory(Integer competition_id, Integer criteria_id, Integer competitor_id, Double points){
        log.info("Adding scoring history for competition ID: " + competition_id + ", criteria ID: " + criteria_id + ", competitor ID: " + competitor_id + ", points: " + points);
        // Get the competition, scoring criterion, and competitor
        Competition competition = getCompetition(competition_id);
        ScoringCriterion scoringCriterion = getScoringCriterion(criteria_id);
        // Check if points given are in the valid range
        if(scoringCriterion.getTotalPoints() < points || points < 0) {
            throw new IllegalArgumentException("Points given have to be between 0 and " + scoringCriterion.getTotalPoints());
        }
        // Check if the competitor is part of the competition
        Competitor competitor = getCompetitor(competitor_id);
        competitor.getCompetitor_competitions().stream().
                filter(competitorCompetition ->
                        Objects.equals(competitorCompetition.getCompetition().getId(), competition_id))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Competitor not found in this competition"));
        // Check if the competition is active
        if(Instant.now().isBefore(competition.getStart_time().toInstant()) || Instant.now().isAfter(competition.getEnd_time().toInstant())) {
            throw new IllegalArgumentException("Competition is not active");
        }
        // Create a new scoring history entry
        ScoringHistory scoringHistory = new ScoringHistory(competitor, competition, scoringCriterion, points, Timestamp.from(Instant.now()));
        scoringHistoryRepository.save(scoringHistory);
    }


    /**
     * Finds the scoring history for a competition.
     *
     * @param competition_id Id of the competition scoring history is being searched for
     * @param auth Authentication object containing the user information who made the request
     * @return CompetitionScoringResponse.Dashboard object containing the scoring history
     */
    public CompetitionScoringResponse.Dashboard getCompetitionScoringHistory(Integer competition_id, Authentication auth){
        log.info("Fetching scoring history for competition ID: " + competition_id);
        // Get the competition and competitors
        Competition competition = getCompetition(competition_id);
        Set<Competitor> competitors = competitorRepository.findByCompetitionId(competition_id);
        // Set the competitors with the correct alias based on the public display name type
        Set<Competitor> competitorsWithCorrectName = setCompetitorAlias(competitors);
        // If the request was made by an admin, show all the scoring history
        if(auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority("admin"))) {
            return setDashboard(competition, competitorsWithCorrectName);
        }
        // If the request was made by a competitor, show only scoring history until the score showtime of the competition
        return setDashboard(competition, competitorsWithCorrectName, competition.getScore_showtime());
    }

    /**
     * querries the scorig history for a specific competitor and score showtime
     *
     * @param competition competition where the scoring results are being searched from
     * @param competitor competitor whose scoring history is being searched for
     * @param score_showtime timestamp of the scoring history up to which the results are being searched
     * @return List of CompetitionScoringResponse.Result objects containing the scoring history
     */
    private List<CompetitionScoringResponse.Result> setResults(Competition competition, Competitor competitor, Timestamp score_showtime) {
        // Get the scoring history for the competitor and score showtime
        Set<ScoringHistory> scoringHistories = scoringHistoryRepository.findByCompetitionIdAndCompetitorIdAndScoreShowtime(competition.getId(), competitor.getId(), score_showtime);
        // Hold the points given for each scoring criterion
        Map<Integer, Double> criterionPoints = new HashMap<>();
        // Hold the results over time
        List<CompetitionScoringResponse.Result> results = new ArrayList<>();
        // Iterate over the scoring history
        for(ScoringHistory scoringHistory : scoringHistories) {
            // If the scoring criterion is not already in the map, add it or update the points given
            criterionPoints.put(scoringHistory.getScoringCriteria().getId(), scoringHistory.getPointsGiven());
            // Calculate the total score for the time of the scoring history
            Double totalScore = 0.0;
            for(Map.Entry<Integer, Double> entry : criterionPoints.entrySet()) {
                totalScore += entry.getValue();
            }
            // Add the result to the list and continue for the next scoring history
            results.add(new CompetitionScoringResponse.Result(scoringHistory.getCreatedAt(), totalScore));
        }
        return results;
    }

    /**
     * Sets the competitors for the competition.
     *
     * @param competition competition where the competitors are being set
     * @param competitors competitors to be set
     * @return Set of CompetitionScoringResponse.Competitor objects containing the competitors and their scoring history
     */
    private Set<CompetitionScoringResponse.Competitor> setCompetitors(Competition competition, Set<Competitor> competitors, Timestamp score_showtime) {
        Set<CompetitionScoringResponse.Competitor> competitorSet = new HashSet<>();
        // Iterate over the competitors
        for (Competitor competitor : competitors) {
            // Get the scoring history for the competitor and score showtime
            List<CompetitionScoringResponse.Result> results = setResults(competition, competitor, score_showtime);
            // If the results are empty, set the score to 0.0
            if (results.isEmpty())
                competitorSet.add(new CompetitionScoringResponse.Competitor(competitor.getAlias(), 0.0, results));
            // If the results are not empty, set the score to the last result
            else
                competitorSet.add(new CompetitionScoringResponse.Competitor(competitor.getAlias(), results.getLast().getPoint_amount(), results));
        }
        return competitorSet;
    }

    /**
     * Sets the dashboard for the competition when the request is made by an admin (score showtime is set as the end time of the competition).
     *
     * @param competition competition where the dashboard is being set
     * @return CompetitionScoringResponse.Dashboard object containing the competitors and their scoring history
     */
    private CompetitionScoringResponse.Dashboard setDashboard(Competition competition, Set<Competitor> competitors) {
        return setDashboard(competition, competitors, competition.getEnd_time());
    }

    /**
     * Sets the dashboard for the competition
     *
     * @param competition competition where the dashboard is being set
     * @return CompetitionScoringResponse.Dashboard object containing the competitors and their scoring history
     */
    private CompetitionScoringResponse.Dashboard setDashboard(Competition competition, Set<Competitor> competitors, Timestamp score_showtime) {
        return new CompetitionScoringResponse.Dashboard(setCompetitors(competition, competitors, score_showtime));
    }

    /**
     * Sets the alias for the competitors based on the public display name type.
     * display name type 1: full name, 2: school name, 3: alias
     *
     * @param competitors competitors to be set
     * @return Set of Competitor objects containing the competitors with their correct alias
     */
    private Set<Competitor> setCompetitorAlias(Set<Competitor> competitors){
        Set<Competitor> competitorsWithCorrectName = new HashSet<>();
        // Iterate over the competitors
        for (Competitor competitor : competitors) {
            // Create a new competitor object with the correct alias based on the public display name type
            // this is to avoid modifying the original competitor object
            Competitor newCompetitor = new Competitor(competitor);
            switch (newCompetitor.getPublic_display_name_type()) {
                case 1 -> newCompetitor.setAlias(competitor.getPersonal_data().getFull_name());
                case 2 -> newCompetitor.setAlias(competitor.getPersonal_data().getSchool().getName());
                case 3 -> newCompetitor.setAlias(competitor.getAlias());
                default ->
                        throw new IllegalStateException("Unexpected value for public display name: " + newCompetitor.getPublic_display_name_type());
            }
            competitorsWithCorrectName.add(newCompetitor);
        }
        return competitorsWithCorrectName;
    }
}

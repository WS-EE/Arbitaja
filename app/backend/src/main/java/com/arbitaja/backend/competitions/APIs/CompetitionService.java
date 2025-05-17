package com.arbitaja.backend.competitions.APIs;

import com.arbitaja.backend.GlobalExceptionHandler;
import com.arbitaja.backend.competitions.dataobjects.Competition;
import com.arbitaja.backend.competitions.repositories.CompetitionRepository;
import com.arbitaja.backend.competitions.scorings.dataobjects.Scoring_groups_structure;
import com.arbitaja.backend.competitions.scorings.repositories.ScoringGroupsStructureRepository;
import com.arbitaja.backend.competitors.APIs.responses.CompetitionResponse;
import com.arbitaja.backend.competitors.dataobjects.Competitor;
import com.arbitaja.backend.competitors.repositories.CompetitorRepository;
import com.arbitaja.backend.users.dataobjects.User;
import com.arbitaja.backend.users.repositories.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CompetitionService {
    private static final Logger log = LogManager.getLogger(CompetitionService.class);


    @Autowired
    private CompetitionRepository competitionRepository;
    @Autowired
    private CompetitorRepository competitorRepository;
    @Autowired
    private ScoringGroupsStructureRepository scoringGroupsStructureRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * Finds all competition in the database
     *
     * @return HTTP response with status 200 and a Set of CompetitionResponse objects
     */
    public ResponseEntity<Set<CompetitionResponse>> getAllCompetitions() {
        // Get all competitions from the database
        List<Competition> competitions = competitionRepository.findAll();
        Set<CompetitionResponse> competitionResponses = new HashSet<>();
        // Iterate through all competitions and create a response object for each
        for (Competition competition : competitions) {
            competitionResponses.add(setCompetitionResponse(competition));
        }
        return ResponseEntity.ok(competitionResponses);
    }

    /**
     * Finds a competition by its id
     *
     * @param competitionId Id of the competition being searched for
     * @return HTTP response with status 200 and CompetitionResponse object if successful
     */
    public ResponseEntity<?> getCompetitionsByCompetitionId(Integer competitionId) {
        // Check if competition with given id exists
        Competition competition = competitionRepository.findByid(competitionId);
        // If competition with given id does not exist, throw NotFoundException
        if (competition == null) {
            throw new GlobalExceptionHandler.NotFoundException("Competition not found");
        }
        return ResponseEntity.ok(setCompetitionResponse(competition));
    }

    /**
     * Finds a competition by its name
     *
     * @param competitionName Name of the competition being searched for
     * @return HTTP response with status 200 and CompetitionResponse object if successful
     */
    public ResponseEntity<?> getCompetitionsByCompetitionName(String competitionName) {
        // Check if competition with given name exists
        Competition competition = competitionRepository.findByName(competitionName);
        // If competition with given name does not exist, throw NotFoundException
        if (competition == null) {
            throw new GlobalExceptionHandler.NotFoundException("Competition not found");
        }
        return ResponseEntity.ok(setCompetitionResponse(competition));
    }

    /**
     * Creates a new competition with given parameters
     *
     * @param competition object with parameters that are used to create the competition
     * @return HTTP response with status 200 and success message if successful
     */
    public ResponseEntity<?> addCompetition(Competition competition) {
        // Check if competition with given name already exists and throw DuplicateException if it does
        if(competitionRepository.findByName(competition.getName()) != null){
            throw new GlobalExceptionHandler.DuplicateException("Competition with this name already exists");
        }
        // Check if competition with given id already exists and throw DuplicateException if it does
        if(competitionRepository.findByid(competition.getId()) != null){
            throw new GlobalExceptionHandler.DuplicateException("Competition with this id already exists");
        }
        // Check if organizer id is given and if it is, check if user with given id exists
        if(competition.getOrganizer_id() != null) {
            User user = getByUsernameOrId(competition);
            if(user == null) throw new GlobalExceptionHandler.NotFoundException("Organizer not found");
            competition.setOrganizer_id(user);
        }
        // Save the competition to the database
        competitionRepository.save(competition);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", "Competition added"));
    }

    /**
     * Updates an existing competition with given parameters
     *
     * @param competition object with parameters that are used to update the competition
     * @return HTTP response with status 200 and success message if successful
     */
    public ResponseEntity<?> updateCompetitionData(CompetitionResponse competition) {
        // Check if competition with given id exists
        Competition updatedCompetition = competitionRepository.findByid(competition.getId());
        // If competition with given id does not exist, throw NotFoundException
        if (updatedCompetition == null) {
            throw new GlobalExceptionHandler.NotFoundException("Competition not found");
        }
        // Check if another competition with given name exists
        if(competitionRepository.findByName(competition.getName()) != null && !competition.getName().equals(updatedCompetition.getName())){
            // If another competition with given name exists, throw DuplicateException
            throw new GlobalExceptionHandler.DuplicateException("Competition with this name already exists");
        }
        // Check if user/organizer with given id exists
        Optional<User> userOptional = userRepository.findById(competition.getOrganizer_id().getUser_id());
        // If user with given id does not exist, throw NotFoundException
        if(userOptional.isEmpty()){
            throw new GlobalExceptionHandler.NotFoundException("Organizer with this id does not exist");
        }
        // Update the competition with given parameters
        updatedCompetition.setName(competition.getName());
        updatedCompetition.setOrganizer_id(userOptional.get());
        updatedCompetition.setStart_time(competition.getStart_time());
        updatedCompetition.setEnd_time(competition.getEnd_time());
        updatedCompetition.setScore_showtime(competition.getScore_showtime());
        updatedCompetition.setPublish_scores(competition.getPublish_scores());
        competitionRepository.save(updatedCompetition);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("success", "Competition updated"));
    }

    /**
     * Deletes a competition with given id from competitions
     *
     * @param competitionId id of the competition to be deleted
     * @return HTTP response with status 200 and success message if successful
     */
    public ResponseEntity<?> deleteCompetition(int competitionId) {
        // Check if competition with given id exists
        Competition competition = competitionRepository.findByid(competitionId);
        // If competition with given id does not exist, throw NotFoundException
        if (competition == null) {
            throw new GlobalExceptionHandler.NotFoundException("Competition not found");
        }
        // Delete the competition
        competitionRepository.delete(competition);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("success", "Competition deleted"));
    }


    /**
     * Creates an Organizer_idResp object from a User object
     *
     * @param user user from witch the organizer_idResp object is created
     * @return new Organizer_idResp object
     */
    private CompetitionResponse.Organizer_idResp setOrganizer(User user) {
        if (user == null) return null;
        return new CompetitionResponse.Organizer_idResp(
                user.getId(),
                user.getPersonal_data().getFull_name(),
                user.getUsername());
    }

    /**
     * Creates a Set of CompetitorResp objects from a Set of Competitor objects in a competition
     *
     * @param competition competition where competitors are being searched from
     * @return new Set of CompetitorResp objects
     */
    private Set<CompetitionResponse.CompetitorResp> setCompetitors(Competition competition) {
        // Get all competitors in the competition
        Set<Competitor> competitors = competitorRepository.findByCompetitionId(competition.getId());
        log.info("competitors in competition {}", competitors);
        Set<CompetitionResponse.CompetitorResp> competitorsResponses = new HashSet<>();
        // Iterate through all competitors and create a response object for each
        for(Competitor competitor : competitors) {
            CompetitionResponse.CompetitorResp competitionResponse =
                    new CompetitionResponse.CompetitorResp(
                    competitor.getId(),
                    competitor.getPersonal_data().getFull_name(),
                    competitor.getAlias());
            // add the competitor response object to the set
            competitorsResponses.add(competitionResponse);
        }
        return competitorsResponses;
    }

    /**
     * Creates a Set of Scoring Groups Structure Response objects from a Set of Scoring Groups Structure objects
     *
     * @param competition competition where scoring groups structures are being searched from
     * @return new Set of Scoring_groups_structure_resp objects
     */
    private Set<CompetitionResponse.Scoring_groups_structure_resp> setScoring_groups_structure_resp(Competition competition) {
        // Get all scoring groups structures in the competition
        Set<Scoring_groups_structure> scoring_groups_structures = scoringGroupsStructureRepository.findAllByCompetitionId(competition.getId());
        log.info("scoring groups structures in competition {}", scoring_groups_structures);

        Set<CompetitionResponse.Scoring_groups_structure_resp> scoring_groups_structure_resps = new HashSet<>();
        // Iterate through all scoring groups structures and create a response object for each
        for(Scoring_groups_structure scoring_groups_structure : scoring_groups_structures) {
            CompetitionResponse.Scoring_groups_structure_resp scoringGroupsStructureResp =
                    new CompetitionResponse.Scoring_groups_structure_resp(scoring_groups_structure.getId(),
                            scoring_groups_structure.getName(), scoring_groups_structure.getDescription(),
                            scoring_groups_structure.getStructure_group_type(),
                            // sets the parent scoring group structure id if it exists
                            scoring_groups_structure.getParent_scoring_groups_structure() == null ? null : scoring_groups_structure.getParent_scoring_groups_structure().getId(),
                            // sets the competitor id if it exists
                            scoring_groups_structure.getCompetitor() == null ? null : scoring_groups_structure.getCompetitor().getId()
                            , scoring_groups_structure.getDynamic_variables());

            // add the scoring group structure response object to the set
            scoring_groups_structure_resps.add(scoringGroupsStructureResp);
        }
        return scoring_groups_structure_resps;
    }

    /**
     * Creates a CompetitionResponse object from a Competition object
     *
     * @param competition competition to be converted
     * @return new CompetitionResponse object
     */
    public CompetitionResponse setCompetitionResponse(Competition competition) {
        return new CompetitionResponse(competition.getId(),
                        competition.getName(),
                        competition.getStart_time(),
                        competition.getEnd_time(),
                        // calls out to setOrganizer method and sets the organizer
                        setOrganizer(competition.getOrganizer_id()),
                        // calls out to setCompetitors method and sets the competitors
                        setCompetitors(competition),
                        competition.getScore_showtime(),
                        // calls out to setScoring_groups_structure_resp method and sets the scoring groups structure for all scoring groups in the competition
                        setScoring_groups_structure_resp(competition),
                        competition.getPublish_scores());
    }

    /**
     * If user with given username or id exists returns the user otherwise returns null
     *
     * @param competition competition where user/organizer is being searched
     * @return Found User or null
     */
    public User getByUsernameOrId(Competition competition){
        User user = null;
        // Check if user with given id exists
        if(competition.getOrganizer_id() != null && competition.getOrganizer_id().getId() != null)
            user = userRepository.findById(competition.getOrganizer_id().getId()).orElse(null);
        // Check if user with given username exists
        if(user == null && competition.getOrganizer_id() != null && competition.getOrganizer_id().getUsername() != null)
            user = userRepository.findByUsername(competition.getOrganizer_id().getUsername()).orElse(null);
        return user;
    }
}

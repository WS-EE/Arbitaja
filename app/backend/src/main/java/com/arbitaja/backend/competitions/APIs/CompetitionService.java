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
    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    public ResponseEntity<Set<CompetitionResponse>> getAllCompetitions() {
        List<Competition> competitions = competitionRepository.findAll();
        Set<CompetitionResponse> competitionResponses = new HashSet<>();
        for (Competition competition : competitions) {
            competitionResponses.add(setCompetitionResponse(competition));
        }
        return ResponseEntity.ok(competitionResponses);
    }

    public ResponseEntity<?> getCompetitionsByCompetitionId(int competitionId) {
        Competition competition = competitionRepository.findByid(competitionId);
        if (competition == null) {
            throw new GlobalExceptionHandler.NotFoundException("Competition not found");
        }
        return ResponseEntity.ok(setCompetitionResponse(competition));
    }

    public ResponseEntity<?> getCompetitionsByCompetitionName(String competitionName) {
        Competition competition = competitionRepository.findByName(competitionName);
        if (competition == null) {
            throw new GlobalExceptionHandler.NotFoundException("Competition not found");
        }
        return ResponseEntity.ok(setCompetitionResponse(competition));
    }

    public ResponseEntity<?> addCompetition(Competition competition) {
        try{
            if(competitionRepository.findByName(competition.getName()) != null){
                throw new GlobalExceptionHandler.DuplicateException("Competition with this name already exists");
            }
            if(competitionRepository.findByid(competition.getId()) != null){
                throw new GlobalExceptionHandler.DuplicateException("Competition with this id already exists");
            }
            if(competition.getOrganizer_id() != null) {
                User user = getByUsernameOrId(competition);
                if(user == null) throw new GlobalExceptionHandler.NotFoundException("Organizer not found");
                competition.setOrganizer_id(user);
            }
            competitionRepository.save(competition);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", "Competition added"));
        } catch (Exception e) {
            log.error(e.getMessage());
            return globalExceptionHandler.handleGeneralException(e);
        }
    }
    public ResponseEntity<?> updateCompetitionData(CompetitionResponse competition) {
        Competition updatedCompetition = competitionRepository.findByid(competition.getId());
        if (updatedCompetition == null) {
            throw new GlobalExceptionHandler.NotFoundException("Competition not found");
        }
        if(competitionRepository.findByName(competition.getName()) != null && !competition.getName().equals(updatedCompetition.getName())){
            throw new GlobalExceptionHandler.DuplicateException("Competition with this name already exists");
        }
        Optional<User> userOptional = userRepository.findById(competition.getOrganizer_id().getUser_id());
        if(userOptional.isEmpty()){
            throw new GlobalExceptionHandler.NotFoundException("Organizer with this id does not exist");
        }
        updatedCompetition.setName(competition.getName());
        updatedCompetition.setOrganizer_id(userOptional.get());
        updatedCompetition.setStart_time(competition.getStart_time());
        updatedCompetition.setEnd_time(competition.getEnd_time());
        updatedCompetition.setScore_showtime(competition.getScore_showtime());
        competitionRepository.save(updatedCompetition);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("success", "Competition updated"));
    }

    public ResponseEntity<?> deleteCompetition(int competitionId) {
        Competition competition = competitionRepository.findByid(competitionId);
        if (competition == null) {
            throw new GlobalExceptionHandler.NotFoundException("Competition not found");
        }
        competitionRepository.delete(competition);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("success", "Competition deleted"));
    }



    private CompetitionResponse.Organizer_idResp setOrganizer(User user) {
        if (user == null) return null;
        CompetitionResponse.Organizer_idResp organizerResp = new CompetitionResponse.Organizer_idResp();
        organizerResp.setFull_name(user.getPersonal_data().getFull_name());
        organizerResp.setUser_id(user.getId());
        organizerResp.setUsername(user.getUsername());
        return organizerResp;
    }

    private Set<CompetitionResponse.CompetitorResp> setCompetitors(Competition competition) {
        Set<Competitor> competitors = competitorRepository.findByCompetitionId(competition.getId());
        log.info("competitors in competition {}", competitors);
        Set<CompetitionResponse.CompetitorResp> competitorsResponses = new HashSet<>();
        for(Competitor competitor : competitors) {
            CompetitionResponse.CompetitorResp competitionResponse = new CompetitionResponse.CompetitorResp();
            competitionResponse.setId(competitor.getId());
            competitionResponse.setAlias(competitor.getAlias());
            competitionResponse.setName(competitor.getPersonal_data().getFull_name());
            competitorsResponses.add(competitionResponse);
        }
        return competitorsResponses;
    }

    private Set<CompetitionResponse.Scoring_groups_structure_resp> setScoring_groups_structure_resp(Set<Scoring_groups_structure> scoring_groups_structures){
        Set<CompetitionResponse.Scoring_groups_structure_resp> scoring_groups_structure_resps = new HashSet<>();
        for(Scoring_groups_structure scoring_groups_structure : scoring_groups_structures) {
            CompetitionResponse.Scoring_groups_structure_resp scoringGroupsStructureResp = new CompetitionResponse.Scoring_groups_structure_resp();
            scoringGroupsStructureResp.setId(scoring_groups_structure.getId());
            scoringGroupsStructureResp.setName(scoring_groups_structure.getName());
            scoringGroupsStructureResp.setDescription(scoring_groups_structure.getDescription());
            scoringGroupsStructureResp.setStructure_group_type(scoring_groups_structure.getStructure_group_type());
            scoringGroupsStructureResp.setScoring_parent_group_id(scoring_groups_structure.getParent_scoring_groups_structure() == null ? null : scoring_groups_structure.getParent_scoring_groups_structure().getId());
            scoringGroupsStructureResp.setCompetitor_id(scoring_groups_structure.getCompetitor() == null ? null : scoring_groups_structure.getCompetitor().getId());
            scoringGroupsStructureResp.setDynamic_variables(scoring_groups_structure.getDynamic_variables());
            scoring_groups_structure_resps.add(scoringGroupsStructureResp);
        }

        return scoring_groups_structure_resps;
    }

    public CompetitionResponse setCompetitionResponse(Competition competition) {
        CompetitionResponse competitionResponse = new CompetitionResponse();
        competitionResponse.setId(competition.getId());
        competitionResponse.setName(competition.getName());
        competitionResponse.setOrganizer_id(setOrganizer(competition.getOrganizer_id()));
        competitionResponse.setCompetitors(setCompetitors(competition));
        competitionResponse.setStart_time(competition.getStart_time());
        competitionResponse.setEnd_time(competition.getEnd_time());
        competitionResponse.setScore_showtime(competition.getScore_showtime());
        Set<Scoring_groups_structure> scoringGroupsStructures = scoringGroupsStructureRepository.findAllByCompetitionId(competition.getId());
        competitionResponse.setScoring_groups(setScoring_groups_structure_resp(scoringGroupsStructures));

        return competitionResponse;
    }

    public User getByUsernameOrId(Competition competition){
        User user = null;
        if(competition.getOrganizer_id() != null && competition.getOrganizer_id().getId() != null) user = userRepository.findById(competition.getOrganizer_id().getId()).orElse(null);
        if(user == null && competition.getOrganizer_id() != null && competition.getOrganizer_id().getUsername() != null) user = userRepository.findByUsername(competition.getOrganizer_id().getUsername()).orElse(null);
        return user;
    }
}

package com.arbitaja.backend.competitions.APIs;

import com.arbitaja.backend.competitions.dataobjects.Competition;
import com.arbitaja.backend.competitions.repositories.CompetitionRepository;
import com.arbitaja.backend.competitions.scorings.dataobjects.Scoring_groups_structure;
import com.arbitaja.backend.competitions.scorings.repositories.ScoringGroupsStructureRepository;
import com.arbitaja.backend.competitors.APIs.responses.CompetitionResponse;
import com.arbitaja.backend.competitors.dataobjects.Competitor;
import com.arbitaja.backend.competitors.repositories.CompetitorRepository;
import com.arbitaja.backend.users.dataobjects.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CompetitionService {
    private static final Logger log = LogManager.getLogger(CompetitionService.class);


    @Autowired
    private CompetitionRepository competitionRepository;
    @Autowired
    private CompetitorRepository competitorRepository;
    @Autowired
    private ScoringGroupsStructureRepository scoringGroupsStructureRepository;

    public ResponseEntity<Set<CompetitionResponse>> getAllCompetitions() {
        List<Competition> competitions = competitionRepository.findAll();
        Set<CompetitionResponse> competitionResponses = competitionResponseSet(competitions);

        return ResponseEntity.ok(competitionResponses);
    }


    private CompetitionResponse.OrganizerResp setOrganizer(User user) {
        CompetitionResponse.OrganizerResp organizerResp = new CompetitionResponse.OrganizerResp();
        organizerResp.setFull_name(user.getPersonal_data().getFull_name());
        organizerResp.setUser_id(user.getId());
        organizerResp.setUsername(user.getUsername());
        return organizerResp;
    }

    private Set<CompetitionResponse.CompetitorResp> setCompetitors(Competition competition) {
        Set<Competitor> competitors = competitorRepository.findByCompetitionId(competition.getId());
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
            scoringGroupsStructureResp.setScoring_parent_croup_id(scoring_groups_structure.getParent_scoring_groups_structure().getId());
            scoringGroupsStructureResp.setCompetitor_id(scoring_groups_structure.getCompetitor().getId());
            scoringGroupsStructureResp.setDynamic_variables(scoring_groups_structure.getDynamic_variables());
            scoring_groups_structure_resps.add(scoringGroupsStructureResp);
        }

        return scoring_groups_structure_resps;
    }

    private Set<CompetitionResponse> competitionResponseSet(List<Competition> competitions) {
        Set<CompetitionResponse> competitionResponses = new HashSet<>();
        for(Competition competition : competitions) {
            CompetitionResponse competitionResponse = new CompetitionResponse();
            competitionResponse.setId(competition.getId());
            competitionResponse.setName(competition.getName());
            competitionResponse.setOrganizer(setOrganizer(competition.getOrganizer_id()));
            competitionResponse.setCompetitors(setCompetitors(competition));
            competitionResponse.setStart_time(competition.getStart_time());
            competitionResponse.setEnd_time(competition.getEnd_time());
            Set<Scoring_groups_structure> scoringGroupsStructures = scoringGroupsStructureRepository.findAllByCompetitionId(competition.getId());
            competitionResponse.setScoring_groups(setScoring_groups_structure_resp(scoringGroupsStructures));
            competitionResponses.add(competitionResponse);
        }

        return competitionResponses;
    }
}

package com.arbitaja.backend.competitors.APIs;

import com.arbitaja.backend.competitions.APIs.CompetitionService;
import com.arbitaja.backend.competitions.dataobjects.Competition;
import com.arbitaja.backend.competitions.repositories.CompetitionRepository;
import com.arbitaja.backend.competitors.APIs.responses.CompetitionResponse;
import com.arbitaja.backend.competitors.dataobjects.Competitor;
import com.arbitaja.backend.competitors.dataobjects.Competitor_competition;
import com.arbitaja.backend.competitors.dataobjects.Personal_data;
import com.arbitaja.backend.competitors.repositories.CompetitorCompetitionRepository;
import com.arbitaja.backend.competitors.repositories.CompetitorRepository;
import com.arbitaja.backend.competitors.repositories.PersonalDataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class CompetitorService {
    private static final Logger log = LogManager.getLogger(CompetitorService.class);

    private final CompetitorRepository competitorRepository;
    private final PersonalDataRepository personalDataRepository;
    private final CompetitionRepository competitionRepository;
    private final CompetitorCompetitionRepository competitorCompetitionRepository;
    private final CompetitionService competitionService;

    @Autowired
    public CompetitorService(CompetitorRepository competitorRepository, PersonalDataRepository personalDataRepository, CompetitionRepository competitionRepository, CompetitorCompetitionRepository competitorCompetitionRepository, CompetitionService competitionService) {
        this.competitorRepository = competitorRepository;
        this.personalDataRepository = personalDataRepository;
        this.competitionRepository = competitionRepository;
        this.competitorCompetitionRepository = competitorCompetitionRepository;
        this.competitionService = competitionService;
    }

    public ResponseEntity<?> addCompetitor(Competitor competitor, Integer competitionId) throws Exception {
        addCompetitor(competitor);
        Competition competition = competitionRepository.findByid(competitionId);
        addCompetitorToCompetition(competition, competitor);
        return new ResponseEntity<>(competitor, HttpStatus.CREATED);
    }

    public void addCompetitor(Competitor competitor) throws Exception {
        log.info("Adding competitor: {}", competitor);
        Competitor localCompetitor = competitorRepository.findByAlias(competitor.getAlias());
        if(localCompetitor != null) throw new Exception("Competitor with alias already exists");
        setCompetitor(competitor, competitor.getAlias(), competitor.getPublic_display_name_type(), competitor.getPersonal_data());
        new ResponseEntity<>(competitor, HttpStatus.CREATED);
    }


    public ResponseEntity<?> editCompetitor(Competitor competitor) throws Exception {
        Optional<Competitor> competitorOpt = competitorRepository.findById(competitor.getId());
        if (competitorOpt.isEmpty()) throw new Exception("Competitor doesn't exists");
        Competitor competitorEdited = competitorOpt.get();
        setCompetitor(competitorEdited, competitor.getAlias(), competitor.getPublic_display_name_type(), competitor.getPersonal_data());
        return new ResponseEntity<>(competitorEdited, HttpStatus.OK);
    }


    private void setCompetitor(Competitor competitor, String alias, Integer public_display_name_type, Personal_data personal_data){
        competitor.setAlias(alias);
        competitor.setPublic_display_name_type(public_display_name_type);
        personal_data = personalDataRepository.findById(personal_data.getId()).orElse(null);
        competitor.setPersonal_data(personal_data);
        competitorRepository.save(competitor);
    }

    public ResponseEntity<?> deleteCompetitor(Integer id) throws Exception {
        Competitor competitor = competitorRepository.findById(id).orElse(null);
        if(competitor == null) throw new Exception("Competitor doesn't exists");
        competitorRepository.delete(competitor);
        return new ResponseEntity<>(Map.of("message", "Competitor deleted successfully"), HttpStatus.OK);
    }

    public ResponseEntity<?> getCompetitor(Integer id) throws Exception {
        Competitor competitor = competitorRepository.findById(id).orElse(null);
        if(competitor == null) throw new Exception("Competitor doesn't exists");
        return new ResponseEntity<>(competitor, HttpStatus.OK);
    }

    public ResponseEntity<?> getCompetitor(String alias) throws Exception {
    Competitor competitor = competitorRepository.findByAlias(alias);
    if(competitor == null) throw new Exception("Competitor doesn't exists");
    return new ResponseEntity<>(competitor, HttpStatus.OK);
    }

    public ResponseEntity<?> getCompetitors(){
        return new ResponseEntity<>(competitorRepository.findAll(), HttpStatus.OK);
    }


    public ResponseEntity<?> addCompetitorToCompetition(Competition competition, Competitor competitor) throws Exception {
        Competitor localCompetitor = getCompetitorFromDatabase(competitor);
        Competition localCompetition = getCompetitionFromDatabase(competition);

        Competitor_competition competitor_competition = new Competitor_competition(localCompetitor, localCompetition);
        competitorCompetitionRepository.save(competitor_competition);
        return new ResponseEntity<>(competitor_competition, HttpStatus.CREATED);
    }


    public ResponseEntity<?> getCompetitorsInCompetition(Integer id){
        Set<Competitor> competitors = competitorRepository.findByCompetitionId(id);
        return new ResponseEntity<>(competitors, HttpStatus.OK);
    }

    public ResponseEntity<?> getCompetitionsForCompetitor(Integer id){
        Set<Competition> competitions = competitionRepository.findByCompetitorId(id);
        Set<CompetitionResponse> competitionResponses = new HashSet<>();
        for(Competition competition : competitions) competitionResponses.add(competitionService.setCompetitionResponse(competition));
        return new ResponseEntity<>(competitionResponses, HttpStatus.OK);
    }

    public ResponseEntity<?> removeCompetitorFromCompetition(Competition competition, Competitor competitor) throws Exception {
        Competitor localCompetitor = getCompetitorFromDatabase(competitor);
        Competition localCompetition = getCompetitionFromDatabase(competition);
        Competitor_competition competitor_competition = competitorCompetitionRepository.findByCompetitorAndCompetition(localCompetitor, localCompetition);
        competitorCompetitionRepository.delete(competitor_competition);
        return new ResponseEntity<>(Map.of("message", "Competitor removed from competition successfully"), HttpStatus.OK);
    }


    private Competitor getCompetitorFromDatabase(Competitor competitor) throws Exception {
        Competitor localCompetitor = null;
        if(competitor.getId() != null) localCompetitor = competitorRepository.findById(competitor.getId()).orElse(null);
        if(localCompetitor == null) localCompetitor = competitorRepository.findByAlias(competitor.getAlias());
        if(localCompetitor == null) throw new Exception("Competitor doesn't exists");
        return localCompetitor;
    }

    private Competition getCompetitionFromDatabase(Competition competition) throws Exception {
        Competition localCompetition = null;
        if(competition.getId() != null) localCompetition = competitionRepository.findByid(competition.getId());
        if(localCompetition == null) localCompetition = competitionRepository.findByName(competition.getName());
        if(localCompetition == null) throw new Exception("Competition doesn't exists");
        return localCompetition;
    }
}

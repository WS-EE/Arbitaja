package com.arbitaja.backend.competitors.APIs;

import com.arbitaja.backend.GlobalExceptionHandler;
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

    /**
     * Used to add a competitor to the database
     * @param competitor Competitor object to be added
     * @param competitionId Competition id to be linked to the competitor
     * @param isLinked Boolean to check if the competitor is linked to an existing personal data
     * @return ResponseEntity with the created competitor
     */
    public ResponseEntity<?> addCompetitor(Competitor competitor, Integer competitionId, Boolean isLinked){
        // Create the competitor
        addCompetitor(competitor, isLinked);
        // If the competitionId is null, return the competitor
        if(competitionId == null) return new ResponseEntity<>(competitor, HttpStatus.CREATED);
        // If the competitionId is not null, add the competitor to the competition
        Competition competition = competitionRepository.findByid(competitionId);
        if(competition == null) throw new GlobalExceptionHandler.NotFoundException("Competition doesn't exists");
        addCompetitorToCompetition(competition, competitor);
        return new ResponseEntity<>(competitor, HttpStatus.CREATED);
    }

    /**
     * Used to add a competitor to the database
     * @param competitor Competitor object to be added
     * @param isLinked Boolean to check if the competitor is linked to an existing personal data
     */
    public void addCompetitor(Competitor competitor, Boolean isLinked){
        log.info("Adding competitor: {}", competitor);
        setCompetitor(competitor, competitor.getAlias(), competitor.getPublic_display_name_type(), competitor.getPersonal_data(), isLinked);
    }


    /**
     * Used to edit a competitor in the database
     * @param competitor Competitor object to be edited
     * @param isLinked Boolean to check if the competitor is linked to an existing personal data
     * @return ResponseEntity with the edited competitor
     */
    public ResponseEntity<?> editCompetitor(Competitor competitor, Boolean isLinked){
        // Check if the competitor exists
        Optional<Competitor> competitorOpt = competitorRepository.findById(competitor.getId());
        if (competitorOpt.isEmpty()) throw new GlobalExceptionHandler.NotFoundException("Competitor doesn't exists");
        Competitor competitorEdited = competitorOpt.get();
        // Set the competitor object
        setCompetitor(competitorEdited, competitor.getAlias(), competitor.getPublic_display_name_type(), competitor.getPersonal_data(), isLinked);
        return new ResponseEntity<>(competitorEdited, HttpStatus.OK);
    }


    /**
     * Used to set the competitor object
     * @param competitor Competitor object to be set
     * @param alias Alias of the competitor
     * @param public_display_name_type Public display name type of the competitor
     * @param personal_data Personal data of the competitor
     * @param isLinked Boolean to check if the competitor is linked to an existing personal data
     */
    private void setCompetitor(Competitor competitor, String alias, Integer public_display_name_type, Personal_data personal_data, Boolean isLinked) {
        // Check if the alias is null or empty
        if (alias == null || alias.isEmpty()) throw new IllegalArgumentException("Alias cannot be null or empty");
        competitor.setAlias(alias);
        competitor.setPublic_display_name_type(public_display_name_type);

        // Link or create personal data
        if(isLinked) {
            // If personal data is linked, check if it exists
            personal_data = personalDataRepository.findById(personal_data.getId()).orElse(null);
            if (personal_data == null) throw new GlobalExceptionHandler.NotFoundException("Personal data doesn't exists");
        } else{
            // If personal data is not linked, create a new one
            personal_data = new Personal_data(personal_data);
            personalDataRepository.save(personal_data);
        }
        // Set the personal data to the competitor
        competitor.setPersonal_data(personal_data);
        competitorRepository.save(competitor);
    }

    /**
     * Used to delete a competitor from the database
     * @param id Competitor id to be deleted
     * @return ResponseEntity with the deleted competitor
     */
    public ResponseEntity<?> deleteCompetitor(Integer id){
        // Check if the competitor exists
        Competitor competitor = competitorRepository.findById(id).orElse(null);
        // If the competitor doesn't exist, throw an exception
        if(competitor == null) throw new GlobalExceptionHandler.NotFoundException("Competitor doesn't exists");
        // If the competitor exists, delete it
        competitorRepository.delete(competitor);
        return new ResponseEntity<>(Map.of("message", "Competitor deleted successfully"), HttpStatus.OK);
    }

    /**
     * Used to get a competitor from the database
     * @param id Competitor id to be retrieved
     * @return ResponseEntity with the retrieved competitor
     */
    public ResponseEntity<?> getCompetitor(Integer id){
        // Check if the competitor exists
        Competitor competitor = competitorRepository.findById(id).orElse(null);
        // If the competitor doesn't exist, throw an exception
        if(competitor == null) throw new GlobalExceptionHandler.NotFoundException("Competitor doesn't exists");
        return new ResponseEntity<>(competitor, HttpStatus.OK);
    }

    /**
     * Used to get all competitors from the database
     * @return ResponseEntity with all competitors
     */
    public ResponseEntity<?> getCompetitors(){
        return new ResponseEntity<>(competitorRepository.findAll(), HttpStatus.OK);
    }


    /**
     * Used to add a competitor to a competition
     * @param competition competition where the competitor will be added
     * @param competitor Competitor object to be added to the competition
     * @return ResponseEntity with the created competitor_competition object
     */
    public ResponseEntity<?> addCompetitorToCompetition(Competition competition, Competitor competitor){
        // Check if the competitor exists
        Competitor localCompetitor = getCompetitorFromDatabase(competitor);
        // Check if the competition exists
        Competition localCompetition = getCompetitionFromDatabase(competition);
        // Check if the competitor is already in the competition
        Competitor_competition competitor_competition = competitorCompetitionRepository.findByCompetitorAndCompetition(localCompetitor, localCompetition);
        // If the competitor is already in the competition, throw an exception
        if(competitor_competition != null) throw new GlobalExceptionHandler.DuplicateException("Competitor already in competition");
        // If the competitor is not in the competition, create a new competitor_competition object
        else competitor_competition = new Competitor_competition(localCompetitor, localCompetition);
        competitorCompetitionRepository.save(competitor_competition);
        return new ResponseEntity<>(competitor_competition, HttpStatus.CREATED);
    }


    /**
     * Used to get all competitors in a competition
     * @param id Competition id to be retrieved
     * @return ResponseEntity with all competitors in the competition
     */
    public ResponseEntity<?> getCompetitorsInCompetition(Integer id){
        Set<Competitor> competitors = competitorRepository.findByCompetitionId(id);
        return new ResponseEntity<>(competitors, HttpStatus.OK);
    }

    /**
     * Used to get all competitions for a competitor
     * @param id Competitor id to be retrieved
     * @return ResponseEntity with all competitions for the competitor
     */
    public ResponseEntity<?> getCompetitionsForCompetitor(Integer id){
        Set<Competition> competitions = competitionRepository.findByCompetitorId(id);
        Set<CompetitionResponse> competitionResponses = new HashSet<>();
        for(Competition competition : competitions) competitionResponses.add(competitionService.setCompetitionResponse(competition));
        return new ResponseEntity<>(competitionResponses, HttpStatus.OK);
    }

    /**
     * Used to remove a competitor from a competition
     * @param competitionId Competition id from where the competitor will be removed
     * @param competitorId Competitor id to be removed
     * @return ResponseEntity with the removed competitor_competition object
     */
    public ResponseEntity<?> removeCompetitorFromCompetition(Integer competitionId, Integer competitorId){
        // Check if the competitor and competition exist
        Optional<Competition> localCompetition = competitionRepository.findById(competitionId);
        if(localCompetition.isEmpty()) throw new GlobalExceptionHandler.NotFoundException("Competition doesn't exists");
        Optional<Competitor> localCompetitor = competitorRepository.findById(competitorId);
        if(localCompetitor.isEmpty()) throw new GlobalExceptionHandler.NotFoundException("Competitor doesn't exists");
        // Check if the competitor is in the competition
        Competitor_competition competitor_competition = competitorCompetitionRepository.findByCompetitorAndCompetition(localCompetitor.get(), localCompetition.get());
        if(competitor_competition == null) throw new GlobalExceptionHandler.NotFoundException("Competitor not in competition");
        // If the competitor is in the competition, delete it
        competitorCompetitionRepository.delete(competitor_competition);
        return new ResponseEntity<>(Map.of("message", "Competitor removed from competition successfully"), HttpStatus.OK);
    }


    /**
     * Used to get a competitor from the database
     * @param competitor Competitor object to be retrieved
     * @return Competitor object from the database
     */
    private Competitor getCompetitorFromDatabase(Competitor competitor){
        Competitor localCompetitor = competitorRepository.findById(competitor.getId()).orElse(null);
        if(localCompetitor == null) throw new GlobalExceptionHandler.NotFoundException("Competitor doesn't exists");
        return localCompetitor;
    }

    /**
     * Used to get a competition from the database
     * @param competition Competition object to be retrieved
     * @return Competition object from the database
     */
    private Competition getCompetitionFromDatabase(Competition competition){
        Competition localCompetition = null;
        if(competition.getId() != null) localCompetition = competitionRepository.findByid(competition.getId());
        if(localCompetition == null) localCompetition = competitionRepository.findByName(competition.getName());
        if(localCompetition == null) throw new GlobalExceptionHandler.NotFoundException("Competition doesn't exists");
        return localCompetition;
    }
}

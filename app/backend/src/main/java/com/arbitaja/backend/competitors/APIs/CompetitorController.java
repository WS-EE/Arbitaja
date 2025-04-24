package com.arbitaja.backend.competitors.APIs;


import com.arbitaja.backend.competitions.dataobjects.Competition;
import com.arbitaja.backend.competitors.APIs.responses.CompetitionResponse;
import com.arbitaja.backend.competitors.dataobjects.Competitor;
import com.arbitaja.backend.competitors.dataobjects.Competitor_competition;
import com.arbitaja.backend.users.APIs.UserController;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

@EnableMethodSecurity
@RestController("competitor")
@RequestMapping("/competitor")
public class CompetitorController {
    private static final Logger log = LogManager.getLogger(CompetitorController.class);

    private final ObjectMapper objectMapper;
    private final CompetitorService competitorService;

    @Autowired
    public CompetitorController(ObjectMapper objectMapper, CompetitorService competitorService) {
        this.objectMapper = objectMapper;
        this.competitorService = competitorService;
    }

    @Transactional
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(
            summary = "Add new competitor",
            description = "Used to add new competitor object and connect it to a personal data",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created competitor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Competitor.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserController.ErrorResponse.class))),
    })
    public ResponseEntity<?> addCompetitor(@RequestBody Competitor competitor, @RequestParam(required = false) Integer competition_id) {
        try {
            ResponseEntity<?> resp = competitorService.addCompetitor(competitor, competition_id);
            log.info("Sending Response for added competitor: " + "{}", objectMapper.writeValueAsString(resp));
            return resp;
        } catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UserController.ErrorResponse(e.getMessage() != null ? e.getMessage() : "An error occurred"));
        }
    }


    @Transactional
    @PutMapping("/edit")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(
            summary = "Edit existing competitor",
            description = "Used to edit an existing competitor",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully edited competitor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Competitor.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserController.ErrorResponse.class))),
    })
    public ResponseEntity<?> editCompetitor(@RequestBody Competitor competitor) {
        try{
            ResponseEntity<?> resp = competitorService.editCompetitor(competitor);
            log.info("Sending Response for edited competitor: " + "{}", objectMapper.writeValueAsString(resp));
            return resp;
        } catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UserController.ErrorResponse(e.getMessage() != null ? e.getMessage() : "An error occurred"));
        }
    }


    @Transactional
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(
            summary = "Delete existing competitor",
            description = "Used to delete an existing competitor",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted competitor",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = "{\"message\": \"Competitor deleted successfully\"}")})),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserController.ErrorResponse.class))),
    })
    public ResponseEntity<?> deleteCompetitor(@RequestParam Integer id) {
        try {
            ResponseEntity<?> resp = competitorService.deleteCompetitor(id);
            log.info("Sending Response for deleted competitor: " + "{}", objectMapper.writeValueAsString(resp));
            return resp;
        } catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UserController.ErrorResponse(e.getMessage() != null ? e.getMessage() : "An error occurred"));
        }
    }

    @GetMapping("/get/all")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(
            summary = "Get all existing competitors",
            description = "Used to get all existing competitor",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully got all competitor",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Competitor.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserController.ErrorResponse.class))),
    })
    public ResponseEntity<?> listCompetitors() {
        try {
            ResponseEntity<?> resp = competitorService.getCompetitors();
            log.info("Sending Response for all competitors: {}", objectMapper.writeValueAsString(resp));
            return resp;
        } catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UserController.ErrorResponse(e.getMessage() != null ? e.getMessage() : "An error occurred"));
        }
    }

    @GetMapping("/get")
    @PreAuthorize("hasAuthority('basic')")
    @Operation(
            summary = "Get existing competitor",
            description = "Used to get an existing competitor",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully got competitor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Competitor.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserController.ErrorResponse.class))),
    })
    public ResponseEntity<?> getCompetitor(@RequestParam(required = false) Integer id, @RequestParam(required = false) String alias) {
        try {
            ResponseEntity<?> resp;
            if (id != null) resp = competitorService.getCompetitor(id);
            else if (alias != null) resp = competitorService.getCompetitor(alias);
            else throw new Exception("required parameters not given");
            log.info("Sending Response for competitor: {}", objectMapper.writeValueAsString(resp));
            return resp;
        } catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UserController.ErrorResponse(e.getMessage() != null ? e.getMessage() : "An error occurred"));
        }
    }

    @Transactional
    @PostMapping("/add/to/competition")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(
            summary = "Add existing competitor to existing competition",
            description = "Used to add a competitor to competition using competitor_competition mapping",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created competitor_competition mapping",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Competitor_competition.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserController.ErrorResponse.class))),
    })
    public ResponseEntity<?> addToCompetition(@RequestBody CompetitionCompetitorWrapper wrapper) {
        try {
            ResponseEntity<?> resp = competitorService.addCompetitorToCompetition(wrapper.getCompetition(), wrapper.getCompetitor());
            log.info("Sending Response for competitor_competition mapping: {}", objectMapper.writeValueAsString(resp));
            return resp;
        } catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UserController.ErrorResponse(e.getMessage() != null ? e.getMessage() : "An error occurred"));
        }
    }

    @Transactional
    @DeleteMapping("/remove/from/competition")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(
            summary = "Remove existing competitor from existing competition",
            description = "Used to remove a competitor from a competition by deleting the competitor_competition mapping",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted competitor_competition mapping",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = "{\"message\": \"\"Competitor removed from competition successfully\"\"}")})),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserController.ErrorResponse.class))),
    })
    public ResponseEntity<?> removeFromCompetition(@RequestBody Competition competition, @RequestBody Competitor competitor) {
        try {
            ResponseEntity<?> resp = competitorService.removeCompetitorFromCompetition(competition, competitor);
            log.info("Sending Response for competitor removal from competition: {}", objectMapper.writeValueAsString(resp));
            return resp;
        } catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UserController.ErrorResponse(e.getMessage() != null ? e.getMessage() : "An error occurred"));
        }
    }

    @GetMapping("/get/all/in/competition")
    @PreAuthorize("hasAuthority('basic')")
    @Operation(
            summary = "Get all existing competitors in the given competition",
            description = "Used to get all existing competitor from a given competition",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully got all competitor",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Competitor.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserController.ErrorResponse.class))),
    })
    public ResponseEntity<?> getInCompetition(@RequestParam Integer id) {
        try {
            ResponseEntity<?> resp = competitorService.getCompetitorsInCompetition(id);
            log.info("Sending Response for competitor in a competition: {}", objectMapper.writeValueAsString(resp));
            return resp;
        } catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UserController.ErrorResponse(e.getMessage() != null ? e.getMessage() : "An error occurred"));
        }
    }

    @GetMapping("/get/all/competitions")
    @PreAuthorize("hasAuthority('basic')")
    @Operation(
            summary = "All of the competitions where the competitor is in",
            description = "Returns all of the Competitions where the competitor is in",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Responds with all of the competitions in JSON array",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CompetitionResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserController.ErrorResponse.class))),
    })
    public ResponseEntity<?> getCompetitionsForCompetitor(@RequestParam Integer id) {
        try {
            ResponseEntity<?> resp = competitorService.getCompetitionsForCompetitor(id);
            log.info("Sending Response for competitions where competitor is in: {}", objectMapper.writeValueAsString(resp));
            return resp;
        } catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UserController.ErrorResponse(e.getMessage() != null ? e.getMessage() : "An error occurred"));
        }
    }
}

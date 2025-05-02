package com.arbitaja.backend.competitions.APIs;


import com.arbitaja.backend.GlobalExceptionHandler;
import com.arbitaja.backend.competitions.dataobjects.Competition;
import com.arbitaja.backend.competitors.APIs.responses.CompetitionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.util.Map;


@EnableMethodSecurity
@RestController("competition")
@RequestMapping("/competition")
public class CompetitionController {
    private static final Logger log = LogManager.getLogger(CompetitionController.class);

    @Autowired
    CompetitionService competitionService;
    @Autowired
    private ObjectMapper objectMapper;


    @GetMapping("/all/get")
    @Operation(
            summary = "All of the competitions",
            description = "Returns all of the Competitions",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Responds with all of the competitions in JSON array",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CompetitionResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
    })
    public ResponseEntity<?> getCompetitions() throws JsonProcessingException {
        ResponseEntity<?> resp = competitionService.getAllCompetitions();
        log.debug("Sending Response for all competitions: " + "{}", objectMapper.writeValueAsString(resp));
        return resp;
    }


    @GetMapping("/get")
    @Operation(
            summary = "Single competition",
            description = "Returns a competition with a corresponding id or name",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Responds with a competition with given id",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CompetitionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Competition not found",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = "{\"error\": \"competition not found\"}")})),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
    })
    public ResponseEntity<?> getCompetition(@RequestParam(required = false) Integer id, @RequestParam(required = false) String name) throws JsonProcessingException {
        ResponseEntity<?> resp;
        if(id != null) resp = competitionService.getCompetitionsByCompetitionId(id);
        else if(name != null) resp = competitionService.getCompetitionsByCompetitionName(name);
        else resp = ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Competition not found"));
        log.debug("Sending Response for competition: " + "{}", objectMapper.writeValueAsString(resp));
        return resp;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(
            summary = "Adds new competition",
            description = "Adds a new competition with given parameters",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Responds with successful creation",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CompetitionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Competition already exists",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = "{\"error\": \"competition with name or id already exists\"}")})),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
    })
    public ResponseEntity<?> addCompetition(@RequestBody Competition competition) throws JsonProcessingException {
        ResponseEntity<?> resp = competitionService.addCompetition(competition);
        log.debug("Sending Response for added competition: " + "{}", objectMapper.writeValueAsString(resp));
        return resp;
    }

    @PutMapping("/edit")
    @PreAuthorize("hasAuthority('admin')")
    @Transactional
    @Operation(
            summary = "Updates competition parameters",
            description = "Updates parameters of a given competition",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Responds with successful update",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = "{\"success\": \"Competition updated\"}")})),
            @ApiResponse(responseCode = "400", description = "Competition already exists",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = "{\"error\": \"competition with name already exists\"}")})),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
    })
    public ResponseEntity<?> updateCompetition(@RequestBody CompetitionResponse competition) throws JsonProcessingException {
        ResponseEntity<?> resp = competitionService.updateCompetitionData(competition);
        log.debug("Sending Response for updated competition: " + "{}", objectMapper.writeValueAsString(resp));
        return resp;
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('admin')")
    @Transactional
    @Operation(
            summary = "Deletes competition",
            description = "Deletes competition with the id",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Responds with successful deletion",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = "{\"success\": \"Competition deleted\"}")})),
            @ApiResponse(responseCode = "404", description = "Competition not found",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = "{\"error\": \"competition not found\"}")})),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
    })
    public ResponseEntity<?> deleteCompetition(@RequestParam Integer id) throws JsonProcessingException {
        ResponseEntity<?> resp = competitionService.deleteCompetition(id);
        log.debug("Sending Response for deleted competition: " + "{}", objectMapper.writeValueAsString(resp));
        return resp;
    }


}

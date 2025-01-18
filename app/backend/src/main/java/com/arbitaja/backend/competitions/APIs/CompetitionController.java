package com.arbitaja.backend.competitions.APIs;


import com.arbitaja.backend.competitors.APIs.responses.CompetitionResponse;
import com.arbitaja.backend.users.APIs.UserController;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@EnableMethodSecurity
@RestController
public class CompetitionController {
    private static final Logger log = LogManager.getLogger(CompetitionController.class);

    @Autowired
    CompetitionService competitionService;


    @GetMapping("/competition/all")
    @PreAuthorize("hasAuthority('basic')")
    @Operation(
            summary = "All of the competitions",
            description = "Returns all of the Competitions",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Responds with all of the competitions in JSON array",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CompetitionResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserController.ErrorResponse.class))),
    })
    public ResponseEntity<?> getCompetition() throws JsonProcessingException {
        try{
            ResponseEntity<?> resp = competitionService.getAllCompetitions();
            log.info("Response: {}", Objects.requireNonNull(resp.getBody()).toString());
            return resp;
        } catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

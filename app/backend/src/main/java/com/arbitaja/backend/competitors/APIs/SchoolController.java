package com.arbitaja.backend.competitors.APIs;

import com.arbitaja.backend.competitors.dataobjects.School;
import com.arbitaja.backend.competitors.repositories.SchoolRepository;
import com.arbitaja.backend.users.APIs.UserController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springdoc.core.service.GenericResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController("school")
@RequestMapping("/school")
public class SchoolController {

    private static final Logger log = LogManager.getLogger(SchoolController.class);

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private GenericResponseService responseBuilder;


    @GetMapping("/all/get")
    @Operation(
            summary = "Get all schools",
            description = "Returns all schools from the database"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns all schools", content =
            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = School.class)))
            )})
    public ResponseEntity<?> schools() throws JsonProcessingException {
        List<School> schools = schoolRepository.findAll();
        List<Map<String, ?>> schoolMap = new ArrayList<>();
        for (School school : schools) {
            schoolMap.add(Map.of("id", school.getId(), "name", school.getName()));
        }
        ResponseEntity<List<Map<String, ?>>> response = ResponseEntity.status(200).body(schoolMap);
        log.info("Sending schools list as response: " + "{}", objectMapper.writeValueAsString(response));
        return response;
    }


    @GetMapping("/get")
    @Operation(
            summary = "Get school by id or name",
            description = "Returns school with id or name"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns school", content =
            @Content(mediaType = "application/json", schema = @Schema(implementation = School.class))
            ),
            @ApiResponse(responseCode = "404", description = "School not found",
            content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(value = "{\"error\": \"School not found\"}")})),
            @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserController.ErrorResponse.class)))
            })
    public ResponseEntity<?> getSchool(@RequestParam Integer id){
        try{
            ResponseEntity<?> resp = schoolService.getSchoolResp(id);
            log.info("Sending school response: " + "{}", objectMapper.writeValueAsString(resp));
            return resp;
        } catch (Exception e) {
            log.error("Failed to register school", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage() != null ? e.getMessage() :"An error occurred"));
        }
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(
            summary = "Register school",
            description = "Used to register new school"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns ok if school was successfully registered", content =
            @Content(mediaType = "application/json", examples = {
                    @ExampleObject(value = "{\"message\": \"School registered successfully\"}")})),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserController.ErrorResponse.class)))
    })
    public ResponseEntity<?> registerSchool(@RequestBody School school) {
        try {
            ResponseEntity<?> resp = schoolService.addSchool(school);
            log.info("Registering school: {}", resp.getBody());
            return resp;
        } catch (Exception e) {
            log.error("Failed to register school", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage() != null ? e.getMessage() :"An error occurred"));
        }
    }


    @DeleteMapping("/register")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(
            summary = "Delete school",
            description = "Used to delete school"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns ok if school was successfully deleted", content =
            @Content(mediaType = "application/json", examples = {
                    @ExampleObject(value = "{\"message\": \"School deleted successfully\"}")})),
            @ApiResponse(responseCode = "404", description = "School not found",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = "{\"error\": \"School not found\"}")})),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserController.ErrorResponse.class)))
    })
    public ResponseEntity<?> deleteSchool(@RequestBody School school) {
        try {
            ResponseEntity<?> resp = schoolService.deleteSchool(school);
            log.info("Deleting school: {}", resp.getBody());
            return resp;
        } catch (Exception e) {
            log.error("Failed to register school", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage() != null ? e.getMessage() :"An error occurred"));
        }
    }

    @PutMapping("/edit")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(
            summary = "Edit school by id",
            description = "Returns updated school"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns school", content =
            @Content(mediaType = "application/json", schema = @Schema(implementation = School.class))
            ),
            @ApiResponse(responseCode = "404", description = "School not found",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = "{\"error\": \"School not found\"}")})),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserController.ErrorResponse.class)))
    })
    public ResponseEntity<?> editSchool(@RequestBody School school) {
        try{
            ResponseEntity<?> resp = schoolService.updateSchool(school);
            log.info("Updating school: {}", resp.getBody());
            return resp;
        } catch (Exception e) {
            log.error("Failed to register school", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage() != null ? e.getMessage() :"An error occurred"));
        }
    }


}

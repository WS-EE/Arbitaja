package com.arbitaja.backend.competitors.APIs;

import com.arbitaja.backend.competitors.dataobjects.School;
import com.arbitaja.backend.competitors.repositories.SchoolRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
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
    private ObjectMapper objectMapper;


    @GetMapping("/all/get")
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

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> registerSchool(@RequestBody School school) {
        try {
            log.info("Registering school: {}", school);
            if(school.getName() == null) throw new Exception("School name cannot be null");
            if(schoolRepository.findSchoolByName(school.getName()).isPresent()) throw new Exception("School already exists");
            school.setCreated_at(Timestamp.from(Instant.now()));
            schoolRepository.save(school);
            return ResponseEntity.status(200).body(Map.of("message", "School registered successfully"));
        } catch (Exception e) {
            log.error("Failed to register school", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage() != null ? e.getMessage() :"An error occurred"));
        }
    }
}

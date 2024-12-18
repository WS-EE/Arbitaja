package com.arbitaja.backend.competitors;

import com.arbitaja.backend.competitors.dataobjects.School;
import com.arbitaja.backend.competitors.repositories.SchoolRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class SchoolController {

    private static final Logger log = LogManager.getLogger(SchoolController.class);

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @GetMapping("/schools")
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
}

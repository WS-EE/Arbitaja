package com.arbitaja.backend.competitors.APIs;

import com.arbitaja.backend.GlobalExceptionHandler;
import com.arbitaja.backend.competitors.dataobjects.School;
import com.arbitaja.backend.competitors.repositories.SchoolRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
public class SchoolService {
    private static final Logger log = LogManager.getLogger(SchoolService.class);

    @Autowired
    private SchoolRepository schoolRepository;

    /**
     * Used to add a new school to the database
     * @param school the school to be added
     * @return a ResponseEntity with a message indicating success or failure
     */
    public ResponseEntity<?> addSchool(School school){
        // Check if the school name is provided
        if(school.getName() == null) throw new IllegalArgumentException("School name cannot be null");
        // Check if the school already exists
        if(schoolRepository.findSchoolByName(school.getName()).isPresent()) throw new IllegalArgumentException("School already exists");
        // Add created_at timestamp and save the school
        school.setCreated_at(Timestamp.from(Instant.now()));
        log.info("Adding school: {}", school);
        schoolRepository.save(school);
        return ResponseEntity.status(200).body(Map.of("message", "School registered successfully"));
    }

    /**
     * Used to update an existing school in the database
     * @param sentSchool the school to be updated
     * @return a ResponseEntity with the updated school
     */
    public ResponseEntity<?> updateSchool(School sentSchool){
        // Check if the school name is provided
        if(sentSchool.getName() == null) throw new IllegalArgumentException("School name cannot be null");
        // Check if the school exists
        School school = getSchool(sentSchool);
        if(school == null) throw new GlobalExceptionHandler.NotFoundException("School not found");
        // Change the school name and update the school
        school.setName(sentSchool.getName());
        log.info("Updating school: {}", school);
        schoolRepository.updateSchool(school);
        return ResponseEntity.status(200).body(school);
    }

    /**
     * Used to delete a school from the database
     * @param id the ID of the school to be deleted
     * @return a ResponseEntity with a message indicating success or failure
     */
    public ResponseEntity<?> deleteSchool(Integer id){
        // Check if the school ID is provided
        School localSchool = getSchool(id);
        if(localSchool == null) throw new GlobalExceptionHandler.NotFoundException("School not found");
        // If school is not null, delete it
        else schoolRepository.delete(localSchool);
        return ResponseEntity.status(200).body(Map.of("message", "School deleted successfully"));
    }

    /**
     * Used to get a school by its ID
     * @param id the ID of the school to be retrieved
     * @return a ResponseEntity with the school
     */
    public ResponseEntity<?> getSchoolResp(Integer id){
        // Check if the school ID is provided
        School localSchool = getSchool(id);
        // If school is not null, return it
        if(localSchool == null) throw new GlobalExceptionHandler.NotFoundException("School not found");
        else return ResponseEntity.status(200).body(localSchool);
    }

    /**
     * Used to get a school by its ID from the database
     * @param sentSchool the school to be retrieved
     * @return the school if found, null otherwise
     */
    public School getSchool(School sentSchool){
        Optional<School> localSchool;
        // Check if the school ID is provided
        if(sentSchool.getId() != null) localSchool = schoolRepository.findById(sentSchool.getId());
        // If the school id is not provided, throw an exception
        else throw new IllegalArgumentException("school id cannot be null");
        return localSchool.orElse(null);
    }

    /**
     * Used to get a school by its ID from the database
     * @param id the ID of the school to be retrieved
     * @return the school if found, null otherwise
     */
    public School getSchool(Integer id){
        Optional<School> localSchool = schoolRepository.findById(id);
        return localSchool.orElse(null);
    }


}

package com.arbitaja.backend.competitors.APIs;

import com.arbitaja.backend.competitors.dataobjects.School;
import com.arbitaja.backend.competitors.repositories.SchoolRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    public ResponseEntity<?> addSchool(School school) throws Exception {
        if(school.getName() == null) throw new Exception("School name cannot be null");
        if(schoolRepository.findSchoolByName(school.getName()).isPresent()) throw new Exception("School already exists");
        school.setCreated_at(Timestamp.from(Instant.now()));
        schoolRepository.save(school);
        return ResponseEntity.status(200).body(Map.of("message", "School registered successfully"));
    }

    public ResponseEntity<?> updateSchool(School sentSchool) throws Exception {
        if(sentSchool.getId() == null) throw new Exception("School id cannot be null");
        if(sentSchool.getName() == null) throw new Exception("School name cannot be null");
        School school = getSchool(sentSchool);
        if(school == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error","School not found"));
        school.setName(sentSchool.getName());
        schoolRepository.updateSchool(school);
        return ResponseEntity.status(200).body(school);
    }

    public ResponseEntity<?> deleteSchool(Integer id) throws Exception {
        School localSchool = getSchool(id);
        if(localSchool == null) ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error","School not found"));
        else schoolRepository.delete(localSchool);
        return ResponseEntity.status(200).body(Map.of("message", "School deleted successfully"));
    }

    public ResponseEntity<?> getSchoolResp(Integer id) throws Exception {
        School localSchool = getSchool(id);
        if(localSchool == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error","School not found"));
        else return ResponseEntity.status(200).body(localSchool);
    }


    public School getSchool(School sentSchool) throws Exception {
        Optional<School> localSchool;
        if(sentSchool.getId() != null) localSchool = schoolRepository.findById(sentSchool.getId());
        else throw new Exception("school id cannot be null");
        return localSchool.orElse(null);
    }

    public School getSchool(Integer id){
        Optional<School> localSchool = schoolRepository.findById(id);
        return localSchool.orElse(null);
    }


}

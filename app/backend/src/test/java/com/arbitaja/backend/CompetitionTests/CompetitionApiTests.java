package com.arbitaja.backend.CompetitionTests;

import com.arbitaja.backend.Generic.GenericTest;
import com.arbitaja.backend.Requests.Competitions.CompetitionAddRequest;
import com.arbitaja.backend.Requests.Competitions.CompetitionGetAllRequest;
import com.arbitaja.backend.Requests.Competitions.CompetitionGetRequest;
import com.arbitaja.backend.Responses.Competitions.CompetitionAddResponse;
import com.arbitaja.backend.Responses.Competitions.CompetitionGetAllResponse;
import com.arbitaja.backend.Responses.Competitions.CompetitionGetResponse;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompetitionApiTests extends GenericTest {
    private static final Logger log = LoggerFactory.getLogger(CompetitionApiTests.class);

    @Test
    void testGetAllCompetition(){
        CompetitionGetAllResponse response = new CompetitionGetAllRequest().send();
        assertEquals(200, response.getStatusCode(), "Should return 200 OK");
    }

    @Test
    void testCreateCompetition(){
        CompetitionAddRequest competitionAddRequest = new CompetitionAddRequest(
                "testCompetition" + new Random().nextInt(1000),
                Timestamp.from(Instant.now().plusSeconds(3600 * 24 * 365)),
                Timestamp.from(Instant.now().plusSeconds(3600 * 24 * 366)),
                Timestamp.from(Instant.now().plusSeconds(3600 * 24 * 365 + 3600)),
                false);
        CompetitionAddResponse competitionAddResponse = competitionAddRequest.send();
        assertEquals(201, competitionAddResponse.getStatusCode(), "Competition should be created successfully");
    }

    @Test
    void testGetCompetitionById(){
        CompetitionGetResponse competitionGetResponse = new CompetitionGetRequest(1).send();
        assertEquals(200, competitionGetResponse.getStatusCode(), "Should return 200 OK");
        assertEquals(1, competitionGetResponse.getId(), "Should return the correct competition ID");
    }

    @Test
    void testGetCompetitionByName(){
        CompetitionGetResponse competitionGetResponse = new CompetitionGetRequest("testCompetition").send();
        assertEquals(200, competitionGetResponse.getStatusCode(), "Should return 200 OK");
        assertEquals("testCompetition", competitionGetResponse.getName(), "Should return the correct competition name");
    }

    @Test
    void testGetCompetitionByIdAndName(){
        CompetitionGetResponse competitionGetResponse = new CompetitionGetRequest(1, "testCompetition").send();
        assertEquals(200, competitionGetResponse.getStatusCode(), "Should return 200 OK");
        assertEquals(1, competitionGetResponse.getId(), "Should return the correct competition ID");
    }

    @Test
    void testGetCompetitionByInvalidId(){
        CompetitionGetResponse competitionGetResponse = new CompetitionGetRequest(9999).send();
        assertEquals(404, competitionGetResponse.getStatusCode(), "Should return 404 Not Found");
        assertEquals("Competition not found", competitionGetResponse.getMessage(), "Should return the correct error message");
    }
}

package com.arbitaja.backend.Requests.Competitions;

import com.arbitaja.backend.Responses.Competitions.CompetitionAddResponse;
import com.arbitaja.backend.Generic.GenericTest;
import io.restassured.response.ValidatableResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

public class CompetitionAddRequest {
    private static final Logger log = LoggerFactory.getLogger(CompetitionAddRequest.class);
    private String name;
    private Timestamp start_time;
    private Timestamp end_time;
    private Timestamp score_showtime;
    private boolean publish_scores;

    public CompetitionAddRequest(String name, Timestamp start_time, Timestamp end_time, Timestamp score_showtime, boolean publish_scores) {
        this.name = name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.score_showtime = score_showtime;
        this.publish_scores = publish_scores;
    }

    public String getName() {
        return name;
    }

    public Timestamp getStart_time() {
        return start_time;
    }

    public Timestamp getEnd_time() {
        return end_time;
    }

    public Timestamp getScore_showtime() {
        return score_showtime;
    }

    public boolean isPublish_scores() {
        return publish_scores;
    }

    public CompetitionAddResponse send() {
        log.info("Sending request to add competition: {}", this);
        CompetitionAddResponse response;
        ValidatableResponse resp = GenericTest.makeRequest("/competition/add", GenericTest.HttpMethod.POST, this, null);
        int statusCode = resp.extract().statusCode();
        if (statusCode == 201) {
            response = new CompetitionAddResponse(statusCode, null, resp.extract().path("success"));
        }
        else {
            String message = resp.extract().path("message");
            String error = resp.extract().path("error");
            response = new CompetitionAddResponse(statusCode, error, message);
        }
        log.info("Got Response to adding competition: {}", response);
        return response;
    }
}

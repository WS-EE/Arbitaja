package com.arbitaja.backend.Requests.Competitions;

import com.arbitaja.backend.Generic.GenericTest;
import com.arbitaja.backend.Responses.Competitions.CompetitionGetAllResponse;
import com.arbitaja.backend.Responses.Competitions.CompetitionGetResponse;
import io.restassured.response.ValidatableResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CompetitionGetAllRequest {

    private static final Logger log = LoggerFactory.getLogger(CompetitionGetAllRequest.class);

    public CompetitionGetAllRequest() {
    }

    public CompetitionGetAllResponse send() {
        log.info("Sending request to get all competitions");
        CompetitionGetAllResponse response;
        ValidatableResponse resp = GenericTest.makeRequest("/competition/all/get", GenericTest.HttpMethod.GET, null, null);
        int statusCode = resp.extract().statusCode();
        if (statusCode == 200) {
            List<CompetitionGetResponse> competitions = resp.extract().jsonPath().getList("", CompetitionGetResponse.class);
            response = new CompetitionGetAllResponse(statusCode, competitions);
        }
        else {
            String message = resp.extract().path("message");
            String error = resp.extract().path("error");
            response = new CompetitionGetAllResponse(statusCode, error, message);
        }
        log.info("Got Response to getting all competitions: {}", response);
        return response;
    }
}

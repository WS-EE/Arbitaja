package com.arbitaja.backend.Requests.Competitions;

import com.arbitaja.backend.Generic.GenericTest;
import com.arbitaja.backend.Responses.Competitions.CompetitionGetResponse;
import io.restassured.response.ValidatableResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CompetitionGetRequest {
    private static final Logger log = LoggerFactory.getLogger(CompetitionGetRequest.class);
    private Integer id;
    private String name;

    public CompetitionGetRequest(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public CompetitionGetRequest(Integer id) {
        this.id = id;
    }

    public CompetitionGetRequest(String name) {
        this.name = name;
    }

    public CompetitionGetRequest() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CompetitionGetRequest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public CompetitionGetResponse send(){
        log.info("Sending request to get competition: {}", this);
        Map<String, String> params;
        if(id != null && name != null) {
            params = Map.of("id", id.toString(), "name", name);
        } else if (id != null) {
            params = Map.of("id", id.toString());
        } else if (name != null) {
            params = Map.of("name", name);
        } else {
            params = null;
        }
        ValidatableResponse resp = GenericTest.makeRequest("/competition/get", GenericTest.HttpMethod.GET, null, params);
        int statusCode = resp.extract().statusCode();
        CompetitionGetResponse response;
        if (statusCode == 200) {
            response = new CompetitionGetResponse(statusCode, resp.extract().as(CompetitionGetResponse.class));
        } else {
            String message = resp.extract().path("message");
            String error = resp.extract().path("error");
            response = new CompetitionGetResponse(statusCode, error, message);
        }
        log.info("Got Response to getting competition: {}", response);
        return response;
    }
}

package com.arbitaja.backend.Generic;

import com.arbitaja.backend.users.dataobjects.User;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class GenericTest {
    private static final Logger log = LoggerFactory.getLogger(GenericTest.class);
    static String sessionCookie;

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost:8080";
        ValidatableResponse resp = given().params("username", "admin", "password", "arbitaja")
                .when().post("/login-user").then().statusCode(302);
        sessionCookie = resp.extract().cookie("JSESSIONID");
    }

    public static User defaultOrganizer(){
        User organizer = new User();
        organizer.setId(1);
        organizer.setUsername("admin");
        return organizer;
    }

    public static ValidatableResponse makeRequest(String uri, HttpMethod method, Object body, Map<String, String> params) {
        RequestSpecification request = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookie("JSESSIONID", sessionCookie);
        if(body != null) {
            request.body(body);
        }
        if (params != null) {
            request.params(params);
        }
        return request.when()
                .request(method.name(), uri)
                .then();
    }


    public enum HttpMethod {
        GET, POST, PUT, DELETE
    }
}

package com.arbitaja.backend.Responses.Competitions;

import java.util.List;

public class CompetitionGetAllResponse {
    private List<CompetitionGetResponse> competitions;
    private int totalCompetitions;
    private int StatusCode;
    private String message;
    private String error;

    public CompetitionGetAllResponse(int responseCode, List<CompetitionGetResponse> competitions) {
        this.competitions = competitions;
        this.totalCompetitions = competitions.size();
        this.StatusCode = responseCode;
    }

    public CompetitionGetAllResponse(int responseCode, String error, String message) {
        this.StatusCode = responseCode;
        this.error = error;
        this.message = message;
    }

    public CompetitionGetAllResponse() {
    }

    public List<CompetitionGetResponse> getCompetitions() {
        return competitions;
    }

    public void setCompetitions(List<CompetitionGetResponse> competitions) {
        this.competitions = competitions;
    }

    public int getTotalCompetitions() {
        return totalCompetitions;
    }

    public void setTotalCompetitions(int totalCompetitions) {
        this.totalCompetitions = totalCompetitions;
    }

    public int getStatusCode() {
        return StatusCode;
    }

    public void setStatusCode(int responseCode) {
        this.StatusCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "CompetitionGetAllResponse{" +
                "competitions=" + competitions +
                ", totalCompetitions=" + totalCompetitions +
                ", responseCode=" + StatusCode +
                ", message='" + message + '\'' +
                '}';
    }
}

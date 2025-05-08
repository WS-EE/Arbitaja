package com.arbitaja.backend.Responses.Competitions;

import com.arbitaja.backend.competitors.APIs.responses.CompetitionResponse;

public class CompetitionGetResponse extends CompetitionResponse {
    int statusCode;
    String error;
    String message;

    public CompetitionGetResponse(int statusCode, String error, String message) {
        this.message = message;
        this.error = error;
        this.statusCode = statusCode;
    }
    public CompetitionGetResponse(int statusCode, CompetitionResponse competitionResponse) {
        super(competitionResponse.getId(), competitionResponse.getName(), competitionResponse.getStart_time(), competitionResponse.getEnd_time(), competitionResponse.getOrganizer_id(), competitionResponse.getCompetitors(), competitionResponse.getScore_showtime(), competitionResponse.getScoring_groups(), competitionResponse.getPublish_scores());
        this.statusCode = statusCode;
    }
    public CompetitionGetResponse() {
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
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
        return "CompetitionGetResponse{" +
                "statusCode=" + statusCode +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", CompetitionResponse='" + super.toString() + '\'' +
                '}';
    }
}

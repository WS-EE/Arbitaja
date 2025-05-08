package com.arbitaja.backend.Responses.Competitions;


public class CompetitionAddResponse {
    private String error;
    private String message;
    private int statusCode;

    public CompetitionAddResponse(int statusCode, String error, String message) {
        this.error = error;
        this.message = message;
        this.statusCode = statusCode;
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

    @Override
    public String toString() {
        return "CompetitionAddResponse{" +
                "message='" + message + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }
}

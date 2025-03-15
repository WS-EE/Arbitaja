package com.arbitaja.backend.competitions.scorings.APIs.Response;



import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public class CompetitionScoringResponse {
    private Dashboard dashboard;

    public CompetitionScoringResponse(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public static class Dashboard {
        private Set<Competitor> competitors;

        public Dashboard(Set<Competitor> competitors) {
            this.competitors = competitors;

        }

        public Set<Competitor> getCompetitors() {
            return competitors;
        }

        public void setCompetitors(Set<Competitor> competitors) {
            this.competitors = competitors;
        }
    }

    public static class Competitor {
        private String name;
        private Double total_score;
        private List<Result> results;

        public Competitor(String name, Double total_score, List<Result> results) {
            this.name = name;
            this.total_score = total_score;
            this.results = results;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getTotal_score() {
            return total_score;
        }

        public void setTotal_score(Double total_score) {
            this.total_score = total_score;
        }

        public List<Result> getResults() {
            return results;
        }

        public void setResults(List<Result> results) {
            this.results = results;
        }

    }

    public static class Result {
        private Timestamp timestamp;
        private Double point_amount;

        public Result(Timestamp timestamp, Double point_amount) {
            this.timestamp = timestamp;
            this.point_amount = point_amount;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
        }

        public Double getPoint_amount() {
            return point_amount;
        }

        public void setPoint_amount(Double point_amount) {
            this.point_amount = point_amount;
        }
    }
}

package com.arbitaja.backend.competitions.scorings.APIs.Response;

import java.util.Set;

public class ScoringCriteriaResultForCompetitors {
    private Integer id;
    private String name;
    private Set<Competitor> competitors;

    public ScoringCriteriaResultForCompetitors(Integer id, String name, Set<Competitor> competitors) {
        this.id = id;
        this.name = name;
        this.competitors = competitors;
    }

    public Integer getid() {
        return id;
    }

    public void setid(Integer id) {
        this.id = id;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public Set<Competitor> getCompetitors() {
        return competitors;
    }

    public void setCompetitors(Set<Competitor> competitors) {
        this.competitors = competitors;
    }

    public static class Competitor{
        private Integer id;
        private String name;
        private Set<Criteria> criterias;

        public Competitor(Integer id, String name, Set<Criteria> criterias) {
            this.id = id;
            this.name = name;
            this.criterias = criterias;
        }

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Set<Criteria> getCriterias() {
            return criterias;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setCriterias(Set<Criteria> criterias) {
            this.criterias = criterias;
        }
    }

    public static class Criteria{
        private static Integer id;
        private static String name;
        private static Double points;

        public Criteria(Integer id, String name, Double points) {
            this.id = id;
            this.name = name;
            this.points = points;
        }

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Double getPoints() {
            return points;
        }

        public static void setId(Integer id) {
            Criteria.id = id;
        }

        public static void setName(String name) {
            Criteria.name = name;
        }

        public static void setPoints(Double points) {
            Criteria.points = points;
        }
    }
}


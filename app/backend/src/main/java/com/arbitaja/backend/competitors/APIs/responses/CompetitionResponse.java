package com.arbitaja.backend.competitors.APIs.responses;


import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;

public class CompetitionResponse {
    @Schema(description = "Unique ID of the competition", example = "69")
    private Integer id;
    @Schema(description = "Name of the competition", example = "Noor meister")
    private String name;
    @Schema(description = "Start time of the competition")
    private Timestamp start_time;
    @Schema(description = "End time of the competition")
    private Timestamp end_time;
    @Schema(description = "Details of the competition organizer_id")
    private Organizer_idResp  organizer_id;
    @Schema(description = "score_showtime of the competition")
    private Timestamp score_showtime;
    @Schema(description = "Competitors of the competition")
    private Set<CompetitorResp> competitors;
    @Schema(description = "Competitors of the competition")
    private Set<Scoring_groups_structure_resp> scoring_groups;
    @Schema(description = "Are scores published")
    private Boolean publish_scores;

    public CompetitionResponse() {
    }
    public CompetitionResponse(Integer id, String name, Timestamp start_time, Timestamp end_time, Organizer_idResp organizer_id, Set<CompetitorResp> competitors, Timestamp score_showtime, Set<Scoring_groups_structure_resp> scoring_groups, Boolean publish_scores) {
        this.id = id;
        this.name = name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.organizer_id = organizer_id;
        this.competitors = competitors;
        this.score_showtime = score_showtime;
        this.scoring_groups = scoring_groups;
        this.publish_scores = publish_scores;
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

    public Organizer_idResp getOrganizer_id() {
        return organizer_id;
    }

    public void setOrganizer_id(Organizer_idResp organizer_id) {
        this.organizer_id = organizer_id;
    }

    public Set<CompetitorResp> getCompetitors() {
        return competitors;
    }

    public void setCompetitors(Set<CompetitorResp> competitors) {
        this.competitors = competitors;
    }

    public Timestamp getStart_time() {
        return start_time;
    }

    public void setStart_time(Timestamp start_time) {
        this.start_time = start_time;
    }

    public Timestamp getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Timestamp end_time) {
        this.end_time = end_time;
    }

    public Set<Scoring_groups_structure_resp> getScoring_groups() {
        return scoring_groups;
    }

    public Timestamp getScore_showtime() {
        return score_showtime;
    }

    public Boolean getPublish_scores() {
        return publish_scores;
    }

    public void setPublish_scores(Boolean publish_scores) {
        this.publish_scores = publish_scores;
    }

    public void setScore_showtime(Timestamp score_showtime) {
        this.score_showtime = score_showtime;
    }

    public void setScoring_groups(Set<Scoring_groups_structure_resp> scoring_groups) {
        this.scoring_groups = scoring_groups;
    }

    public static class Organizer_idResp{
        private Integer user_id;
        private String full_name;
        private String username;

        public Organizer_idResp() {
        }
        public Organizer_idResp(Integer user_id, String full_name, String username) {
            this.user_id = user_id;
            this.full_name = full_name;
            this.username = username;
        }

        public Integer getUser_id() {
            return user_id;
        }

        public void setUser_id(Integer user_id) {
            this.user_id = user_id;
        }

        public String getFull_name() {
            return full_name;
        }

        public void setFull_name(String full_name) {
            this.full_name = full_name;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        @Override
        public String toString() {
            return "Organizer_idResp{" +
                    "user_id=" + user_id +
                    ", full_name='" + full_name + '\'' +
                    ", username='" + username + '\'' +
                    '}';
        }
    }

    public static class CompetitorResp{
        private Integer id;
        private String name;
        private String alias;

        public CompetitorResp() {
        }

        public CompetitorResp(Integer id, String name, String alias) {
            this.id = id;
            this.name = name;
            this.alias = alias;
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

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        @Override
        public String toString() {
            return "CompetitorResp{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", alias='" + alias + '\'' +
                    '}';
        }
    }
    public static class Scoring_groups_structure_resp{
        private Integer id;
        private String name;
        private String description;
        private Integer competitor_id;
        private Integer scoring_parent_group_id;
        private Integer structure_group_type;
        private Map<String, ?> dynamic_variables;
        public Scoring_groups_structure_resp() {}

        public Scoring_groups_structure_resp(int id, String name, String description, Integer competitor_id, Integer scoring_parent_group_id, Integer structure_group_type, Map<String, ?> dynamic_variables) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.competitor_id = competitor_id;
            this.scoring_parent_group_id = scoring_parent_group_id;
            this.structure_group_type = structure_group_type;
            this.dynamic_variables = dynamic_variables;
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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Integer getCompetitor_id() {
            return competitor_id;
        }

        public void setCompetitor_id(Integer competitor_id) {
            this.competitor_id = competitor_id;
        }

        public Integer getScoring_parent_group_id() {
            return scoring_parent_group_id;
        }

        public void setScoring_parent_group_id(Integer scoring_parent_group_id) {
            this.scoring_parent_group_id = scoring_parent_group_id;
        }

        public Integer getStructure_group_type() {
            return structure_group_type;
        }

        public void setStructure_group_type(Integer structure_group_type) {
            this.structure_group_type = structure_group_type;
        }

        public Map<String, ?> getDynamic_variables() {
            return dynamic_variables;
        }

        public void setDynamic_variables(Map<String, ?> dynamic_variables) {
            this.dynamic_variables = dynamic_variables;
        }

        @Override
        public String toString() {
            return "Scoring_groups_structure_resp{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", competitor_id=" + competitor_id +
                    ", scoring_parent_group_id=" + scoring_parent_group_id +
                    ", structure_group_type=" + structure_group_type +
                    ", dynamic_variables=" + dynamic_variables +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "CompetitionResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", start_time=" + start_time +
                ", end_time=" + end_time +
                ", organizer_id=" + organizer_id +
                ", score_showtime=" + score_showtime +
                ", competitors=" + competitors +
                ", scoring_groups=" + scoring_groups +
                ", publish_scores=" + publish_scores +
                '}';
    }
}

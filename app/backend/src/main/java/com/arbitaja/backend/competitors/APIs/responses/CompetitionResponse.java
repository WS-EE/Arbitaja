package com.arbitaja.backend.competitors.APIs.responses;


import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;

public class CompetitionResponse {
    @Schema(description = "Unique ID of the competition", example = "69")
    private int id;
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

    public CompetitionResponse() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
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

    public void setScore_showtime(Timestamp score_showtime) {
        this.score_showtime = score_showtime;
    }

    public void setScoring_groups(Set<Scoring_groups_structure_resp> scoring_groups) {
        this.scoring_groups = scoring_groups;
    }

    public static class Organizer_idResp{
        private int user_id;
        private String full_name;
        private String username;

        public Organizer_idResp() {
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
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
    }

    public static class CompetitorResp{
        private int id;
        private String name;
        private String alias;

        public CompetitorResp() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
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
    }
    public static class Scoring_groups_structure_resp{
        private int id;
        private String name;
        private String description;
        private Integer competitor_id;
        private int scoring_parent_group_id;
        private int structure_group_type;
        private Map<String, ?> dynamic_variables;
        public Scoring_groups_structure_resp() {}

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

        public int getScoring_parent_group_id() {
            return scoring_parent_group_id;
        }

        public void setScoring_parent_group_id(Integer scoring_parent_group_id) {
            this.scoring_parent_group_id = scoring_parent_group_id;
        }

        public int getStructure_group_type() {
            return structure_group_type;
        }

        public void setStructure_group_type(int structure_group_type) {
            this.structure_group_type = structure_group_type;
        }

        public Map<String, ?> getDynamic_variables() {
            return dynamic_variables;
        }

        public void setDynamic_variables(Map<String, ?> dynamic_variables) {
            this.dynamic_variables = dynamic_variables;
        }
    }
}

package com.arbitaja.backend.competitors.APIs.responses;


import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.Time;
import java.util.Map;
import java.util.Set;

public class CompetitionResponse {
    @Schema(description = "Unique ID of the competition", example = "69")
    private int id;
    @Schema(description = "Name of the competition", example = "Noor meister")
    private String name;
    @Schema(description = "Start time of the competition", example = "08:00:00")
    private Time start_time;
    @Schema(description = "End time of the competition", example = "17:00:00")
    private Time end_time;
    @Schema(description = "Details of the competition organizer")
    private OrganizerResp  organizer;
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

    public OrganizerResp getOrganizer() {
        return organizer;
    }

    public void setOrganizer(OrganizerResp organizer) {
        this.organizer = organizer;
    }

    public Set<CompetitorResp> getCompetitors() {
        return competitors;
    }

    public void setCompetitors(Set<CompetitorResp> competitors) {
        this.competitors = competitors;
    }

    public Time getStart_time() {
        return start_time;
    }

    public void setStart_time(Time start_time) {
        this.start_time = start_time;
    }

    public Time getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Time end_time) {
        this.end_time = end_time;
    }

    public Set<Scoring_groups_structure_resp> getScoring_groups() {
        return scoring_groups;
    }

    public void setScoring_groups(Set<Scoring_groups_structure_resp> scoring_groups) {
        this.scoring_groups = scoring_groups;
    }

    public static class OrganizerResp{
        private int user_id;
        private String full_name;
        private String username;

        public OrganizerResp() {
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
        private int competitor_id;
        private int scoring_parent_croup_id;
        private int structure_group_type;
        private Map<String, ?> dynamic_variables;
        public Scoring_groups_structure_resp() {}

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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getCompetitor_id() {
            return competitor_id;
        }

        public void setCompetitor_id(int competitor_id) {
            this.competitor_id = competitor_id;
        }

        public int getScoring_parent_croup_id() {
            return scoring_parent_croup_id;
        }

        public void setScoring_parent_croup_id(int scoring_parent_croup_id) {
            this.scoring_parent_croup_id = scoring_parent_croup_id;
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

package com.arbitaja.backend.competitions.dataobjects;


import com.arbitaja.backend.competitions.scorings.dataobjects.Scoring_groups_structure;
import com.arbitaja.backend.competitors.dataobjects.Competitor_competition;
import com.arbitaja.backend.users.dataobjects.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.sql.Time;
import java.util.List;

@Entity
@Table(name = "competition")
@Schema(description = "Details of a competition")
public class Competition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique ID of the competition", example = "69")
    private int id;

    @Column(name = "name")
    @Schema(description = "Name of the competition", example = "Noor meister")
    private String name;

    @OneToOne
    @JoinColumn(name = "scoring_criteria_group_main_id")
    @Schema(description = "Scoring group structure details")
    private Scoring_groups_structure scoring_groups_structure;

    @Column(name = "start_time")
    @Schema(description = "Start time of the competition", example = "08:00:00")
    private Time start_time;

    @Column(name = "end_time")
    @Schema(description = "End time of the competition", example = "17:00:00")
    private Time end_time;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organizer_id")
    @Schema(description = "Details of the competition organizer")
    private User organizer_id;

    @OneToMany(mappedBy = "competition")
    @Schema(description = "List of competitors in the competition")
    @JsonIgnore
    private List<Competitor_competition> competitorCompetitions;

    public Competition() {}

    public Competition(String name, Scoring_groups_structure scoring_groups_structure, Time start_time, Time end_time, User organizer_id) {
        this.name = name;
        this.scoring_groups_structure = scoring_groups_structure;
        this.start_time = start_time;
        this.end_time = end_time;
        this.organizer_id = organizer_id;
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

    public Scoring_groups_structure getScoring_groups_structure() {
        return scoring_groups_structure;
    }

    public void setScoring_groups_structure(Scoring_groups_structure scoring_groups_structure) {
        this.scoring_groups_structure = scoring_groups_structure;
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

    public User getOrganizer_id() {
        return organizer_id;
    }

    public void setOrganizer_id(User organizer_id) {
        this.organizer_id = organizer_id;
    }

    public List<Competitor_competition> getCompetitorCompetitions() {
        return competitorCompetitions;
    }

    public void setCompetitorCompetitions(List<Competitor_competition> competitorCompetitions) {
        this.competitorCompetitions = competitorCompetitions;
    }

    @Override
    public String toString() {
        return "Competition{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", start_time=" + start_time +
                ", end_time=" + end_time +
                ", organizer_id=" + organizer_id +
                '}';
    }
}

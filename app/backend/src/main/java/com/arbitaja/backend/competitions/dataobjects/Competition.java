package com.arbitaja.backend.competitions.dataobjects;


import com.arbitaja.backend.competitions.scorings.dataobjects.Scoring_groups_structure;
import com.arbitaja.backend.competitors.dataobjects.Competitor_competition;
import jakarta.persistence.*;

import java.sql.Time;
import java.util.List;

@Entity
@Table(name = "competition")
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;
    @OneToOne
    @JoinColumn(name = "scoring_criteria_group_main_id")
    private Scoring_groups_structure scoring_groups_structure;
    @Column(name = "start_time")
    private Time start_time;
    @Column(name = "end_time")
    private Time end_time;

    @OneToMany(mappedBy = "competition")
    private List<Competitor_competition> competitorCompetitions;
}

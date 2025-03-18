package com.arbitaja.backend.competitions.scorings.dataobjects;

import com.arbitaja.backend.competitions.dataobjects.Competition;
import jakarta.persistence.*;

@Entity
@Table(name = "competition_scoring_criteria")
public class CompetitionScoringCriterion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id")
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criteria_id")
    private ScoringCriterion criteria;

    public CompetitionScoringCriterion() {
    }

    public CompetitionScoringCriterion(Competition competition, ScoringCriterion criteria) {
        this.competition = competition;
        this.criteria = criteria;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public ScoringCriterion getCriteria() {
        return criteria;
    }

    public void setCriteria(ScoringCriterion criteria) {
        this.criteria = criteria;
    }

}
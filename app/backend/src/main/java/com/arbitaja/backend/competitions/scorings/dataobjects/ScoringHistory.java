package com.arbitaja.backend.competitions.scorings.dataobjects;

import com.arbitaja.backend.competitions.dataobjects.Competition;
import com.arbitaja.backend.competitors.dataobjects.Competitor;
import com.arbitaja.backend.users.dataobjects.User;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "scoring_history")
public class ScoringHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competitor_id")
    private Competitor competitor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id")
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scoring_host_id")
    private ScoringHost scoringHost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scoring_criteria_id")
    private ScoringCriterion scoringCriteria;

    @Column(name = "points_given")
    private Double pointsGiven;

    @Column(name = "result")
    private String result;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    private User addedByUser;

    public ScoringHistory(Competitor competitor, Competition competition, ScoringCriterion scoringCriteria, Double pointsGiven, Timestamp createdAt) {
        this.competitor = competitor;
        this.competition = competition;
        this.scoringCriteria = scoringCriteria;
        this.pointsGiven = pointsGiven;
        this.createdAt = createdAt;
    }

    public ScoringHistory() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Competitor getCompetitor() {
        return competitor;
    }

    public void setCompetitor(Competitor competitor) {
        this.competitor = competitor;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public ScoringHost getScoringHost() {
        return scoringHost;
    }

    public void setScoringHost(ScoringHost scoringHost) {
        this.scoringHost = scoringHost;
    }

    public ScoringCriterion getScoringCriteria() {
        return scoringCriteria;
    }

    public void setScoringCriteria(ScoringCriterion scoringCriteria) {
        this.scoringCriteria = scoringCriteria;
    }

    public Double getPointsGiven() {
        return pointsGiven;
    }

    public void setPointsGiven(Double pointsGiven) {
        this.pointsGiven = pointsGiven;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }

    public User getAddedByUser() {
        return addedByUser;
    }

    public void setAddedByUser(User addedByUser) {
        this.addedByUser = addedByUser;
    }

    @Override
    public String toString() {
        return "ScoringHistory{" +
                "id=" + id +
                ", competitor=" + competitor +
                ", competition=" + competition +
                ", scoringHost=" + scoringHost +
                ", scoringCriteria=" + scoringCriteria +
                ", pointsGiven=" + pointsGiven +
                ", result='" + result + '\'' +
                ", createdAt=" + createdAt +
                ", deletedAt=" + deletedAt +
                ", addedByUser=" + addedByUser +
                '}';
    }
}
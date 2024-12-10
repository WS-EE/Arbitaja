package com.arbitaja.backend.competitions.scorings.dataobjects;

import jakarta.persistence.*;

@Entity
@Table(name = "logical_group_link")
public class LogicalGroupLink {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logical_group_id")
    private ScoringLogicalGroup logicalGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scoring_host_id")
    private ScoringHost scoringHost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scoring_criteria_id")
    private ScoringCriterion scoringCriteria;

    public LogicalGroupLink(ScoringLogicalGroup logicalGroup, ScoringHost scoringHost, ScoringCriterion scoringCriteria) {
        this.logicalGroup = logicalGroup;
        this.scoringHost = scoringHost;
        this.scoringCriteria = scoringCriteria;
    }

    public LogicalGroupLink() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ScoringLogicalGroup getLogicalGroup() {
        return logicalGroup;
    }

    public void setLogicalGroup(ScoringLogicalGroup logicalGroup) {
        this.logicalGroup = logicalGroup;
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

    @Override
    public String toString() {
        return "LogicalGroupLink{" +
                "id=" + id +
                ", logicalGroup=" + logicalGroup +
                ", scoringHost=" + scoringHost +
                ", scoringCriteria=" + scoringCriteria +
                '}';
    }
}
package com.arbitaja.backend.competitions.scorings.dataobjects;

import com.arbitaja.backend.agents.dataobjects.ScoringAgent;
import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "scoring_criteria")
public class ScoringCriterion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scoring_host_id")
    private ScoringHost scoringHost;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_manual")
    private Boolean isManual;

    @Column(name = "total_points")
    private Double totalPoints;

    @Column(name = "is_generalized")
    private Boolean isGeneralized;

    @Column(name = "expected_result")
    private String expectedResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criteria_template_id")
    private ScoringCriterion criteriaTemplate;

    @Column(name = "is_template")
    private Boolean isTemplate;

    @OneToMany(mappedBy = "scoringCriteria")
    private Set<LogicalGroupLink> logicalGroupLinks = new LinkedHashSet<>();

    @OneToMany(mappedBy = "scoringCriteria")
    private Set<ScoringAgent> scoringAgents = new LinkedHashSet<>();

    @OneToMany(mappedBy = "criteriaTemplate")
    private Set<ScoringCriterion> scoringCriteria = new LinkedHashSet<>();

    @OneToMany(mappedBy = "scoringCriteria")
    private Set<ScoringHistory> scoringHistories = new LinkedHashSet<>();

    public ScoringCriterion(ScoringHost scoringHost, String name, String description, Boolean isManual, Double totalPoints, Boolean isGeneralized, String expectedResult, ScoringCriterion criteriaTemplate, Boolean isTemplate) {
        this.scoringHost = scoringHost;
        this.name = name;
        this.description = description;
        this.isManual = isManual;
        this.totalPoints = totalPoints;
        this.isGeneralized = isGeneralized;
        this.expectedResult = expectedResult;
        this.criteriaTemplate = criteriaTemplate;
        this.isTemplate = isTemplate;
    }

    public ScoringCriterion() {
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ScoringHost getScoringHost() {
        return scoringHost;
    }

    public void setScoringHost(ScoringHost scoringHost) {
        this.scoringHost = scoringHost;
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

    public Boolean getIsManual() {
        return isManual;
    }

    public void setIsManual(Boolean isManual) {
        this.isManual = isManual;
    }

    public Double getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Double totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Boolean getIsGeneralized() {
        return isGeneralized;
    }

    public void setIsGeneralized(Boolean isGeneralized) {
        this.isGeneralized = isGeneralized;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public ScoringCriterion getCriteriaTemplate() {
        return criteriaTemplate;
    }

    public void setCriteriaTemplate(ScoringCriterion criteriaTemplate) {
        this.criteriaTemplate = criteriaTemplate;
    }

    public Boolean getIsTemplate() {
        return isTemplate;
    }

    public void setIsTemplate(Boolean isTemplate) {
        this.isTemplate = isTemplate;
    }

    public Set<LogicalGroupLink> getLogicalGroupLinks() {
        return logicalGroupLinks;
    }

    public void setLogicalGroupLinks(Set<LogicalGroupLink> logicalGroupLinks) {
        this.logicalGroupLinks = logicalGroupLinks;
    }

    public Set<ScoringAgent> getScoringAgents() {
        return scoringAgents;
    }

    public void setScoringAgents(Set<ScoringAgent> scoringAgents) {
        this.scoringAgents = scoringAgents;
    }

    public Set<ScoringCriterion> getScoringCriteria() {
        return scoringCriteria;
    }

    public void setScoringCriteria(Set<ScoringCriterion> scoringCriteria) {
        this.scoringCriteria = scoringCriteria;
    }

    public Set<ScoringHistory> getScoringHistories() {
        return scoringHistories;
    }

    public void setScoringHistories(Set<ScoringHistory> scoringHistories) {
        this.scoringHistories = scoringHistories;
    }

    @Override
    public String toString() {
        return "ScoringCriterion{" +
                "id=" + id +
                ", scoringHost=" + scoringHost +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isManual=" + isManual +
                ", totalPoints=" + totalPoints +
                ", isGeneralized=" + isGeneralized +
                ", expectedResult='" + expectedResult + '\'' +
                ", criteriaTemplate=" + criteriaTemplate +
                ", isTemplate=" + isTemplate +
                ", logicalGroupLinks=" + logicalGroupLinks +
                ", scoringAgents=" + scoringAgents +
                ", scoringCriteria=" + scoringCriteria +
                ", scoringHistories=" + scoringHistories +
                '}';
    }
}
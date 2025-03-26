package com.arbitaja.backend.competitions.scorings.dataobjects;

import com.arbitaja.backend.agents.dataobjects.ScoringAgent;
import com.arbitaja.backend.competitions.scorings.APIs.Request.ScoringCriteriaAdd;
import com.arbitaja.backend.competitions.scorings.APIs.ScoringCriterionDeserializer;
import com.arbitaja.backend.competitions.scorings.APIs.ScoringCriterionSerializer;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "scoring_criteria")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ScoringCriterion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scoring_host_id")
    @JsonIgnore
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
    @JsonSerialize(using = ScoringCriterionSerializer.class)
    @JsonDeserialize(using = ScoringCriterionDeserializer.class)
    private ScoringCriterion criteriaTemplate;

    @Column(name = "is_template")
    private Boolean isTemplate;

    @Column(name = "visibility_level")
    private int visibilityLevel;

    @JsonIgnore
    @OneToMany(mappedBy = "scoringCriteria")
    private Set<LogicalGroupLink> logicalGroupLinks = new LinkedHashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "scoringCriteria")
    private Set<ScoringAgent> scoringAgents = new LinkedHashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "criteriaTemplate")
    private Set<ScoringCriterion> scoringCriteria = new LinkedHashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "scoringCriteria")
    private Set<ScoringHistory> scoringHistories = new LinkedHashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "criteria", cascade = CascadeType.REMOVE)
    private Set<CompetitionScoringCriterion> competitionScoringCriteria = new LinkedHashSet<>();

    public ScoringCriterion(String name, String description, Boolean isManual, Double totalPoints, Boolean isGeneralized, String expectedResult, Boolean isTemplate) {
        this.name = name;
        this.description = description;
        this.isManual = isManual;
        this.totalPoints = totalPoints;
        this.isGeneralized = isGeneralized;
        this.expectedResult = expectedResult;
        this.isTemplate = isTemplate;
    }

    public ScoringCriterion(ScoringCriteriaAdd scoringCriteriaAdd){
        this.name = scoringCriteriaAdd.getName();
        this.description = scoringCriteriaAdd.getDescription();
        this.isManual = scoringCriteriaAdd.getManual();
        this.totalPoints = scoringCriteriaAdd.getTotalPoints();
        this.isGeneralized = scoringCriteriaAdd.getGeneralized();
        this.expectedResult = scoringCriteriaAdd.getExpectedResult();
        this.isTemplate = scoringCriteriaAdd.getTemplate();
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
                ", scoringHost=" + (scoringHost != null ? scoringHost.getId() : "null") +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isManual=" + isManual +
                ", totalPoints=" + totalPoints +
                ", isGeneralized=" + isGeneralized +
                ", expectedResult='" + expectedResult + '\'' +
                ", criteriaTemplate=" + (criteriaTemplate != null ? criteriaTemplate.getId() : "null") +
                ", isTemplate=" + isTemplate +
                ", visibilityLevel=" + visibilityLevel +
                '}';
    }
}
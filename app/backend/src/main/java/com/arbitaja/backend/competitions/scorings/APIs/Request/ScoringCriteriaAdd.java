package com.arbitaja.backend.competitions.scorings.APIs.Request;


import jakarta.persistence.*;

public class ScoringCriteriaAdd {
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

    @Column(name = "criteria_template")
    private Integer criteriaTemplate;

    @Column(name = "is_template")
    private Boolean isTemplate;

    @Column(name = "visibility_level")
    private Integer visibilityLevel;

    @Column(name = "competition_id")
    private Integer competitionId;


    public ScoringCriteriaAdd() {
    }

    public ScoringCriteriaAdd(String name, String description, Boolean isManual, Double totalPoints, Boolean isGeneralized, String expectedResult, Integer criteriaTemplate, Boolean isTemplate, Integer visibilityLevel, Integer competitionId) {
        this.name = name;
        this.description = description;
        this.isManual = isManual;
        this.totalPoints = totalPoints;
        this.isGeneralized = isGeneralized;
        this.expectedResult = expectedResult;
        this.criteriaTemplate = criteriaTemplate;
        this.isTemplate = isTemplate;
        this.visibilityLevel = visibilityLevel;
        this.competitionId = competitionId;
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

    public Boolean getManual() {
        return isManual;
    }

    public void setManual(Boolean manual) {
        isManual = manual;
    }

    public Double getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Double totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Boolean getGeneralized() {
        return isGeneralized;
    }

    public void setGeneralized(Boolean generalized) {
        isGeneralized = generalized;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public Integer getCriteriaTemplate() {
        return criteriaTemplate;
    }

    public void setCriteriaTemplate(Integer criteriaTemplate) {
        this.criteriaTemplate = criteriaTemplate;
    }

    public Boolean getTemplate() {
        return isTemplate;
    }

    public void setTemplate(Boolean template) {
        isTemplate = template;
    }

    public Integer getVisibilityLevel() {
        return visibilityLevel;
    }

    public void setVisibilityLevel(Integer visibilityLevel) {
        this.visibilityLevel = visibilityLevel;
    }

    public Integer getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Integer competitionId) {
        this.competitionId = competitionId;
    }
}

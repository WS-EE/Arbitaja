package com.arbitaja.backend;

import com.arbitaja.backend.competitions.scorings.dataobjects.ScoringCriterion;
import jakarta.persistence.*;

@Entity
@Table(name = "criteria_dependency")
public class CriteriaDependency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criteria_id")
    private ScoringCriterion criteria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dependent_on_criteria_id")
    private ScoringCriterion dependentOnCriteria;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ScoringCriterion getCriteria() {
        return criteria;
    }

    public void setCriteria(ScoringCriterion criteria) {
        this.criteria = criteria;
    }

    public ScoringCriterion getDependentOnCriteria() {
        return dependentOnCriteria;
    }

    public void setDependentOnCriteria(ScoringCriterion dependentOnCriteria) {
        this.dependentOnCriteria = dependentOnCriteria;
    }

}
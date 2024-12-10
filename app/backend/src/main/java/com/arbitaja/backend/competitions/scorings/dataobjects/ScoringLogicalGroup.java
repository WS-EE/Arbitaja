package com.arbitaja.backend.competitions.scorings.dataobjects;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "scoring_logical_groups")
public class ScoringLogicalGroup {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "is_generalized")
    private Boolean isGeneralized;

    @OneToMany(mappedBy = "logicalGroup")
    private Set<LogicalGroupLink> logicalGroupLinks = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsGeneralized() {
        return isGeneralized;
    }

    public void setIsGeneralized(Boolean isGeneralized) {
        this.isGeneralized = isGeneralized;
    }

    public Set<LogicalGroupLink> getLogicalGroupLinks() {
        return logicalGroupLinks;
    }

    public void setLogicalGroupLinks(Set<LogicalGroupLink> logicalGroupLinks) {
        this.logicalGroupLinks = logicalGroupLinks;
    }

}
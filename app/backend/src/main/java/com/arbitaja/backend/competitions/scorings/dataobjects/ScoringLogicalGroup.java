package com.arbitaja.backend.competitions.scorings.dataobjects;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "scoring_logical_groups")
public class ScoringLogicalGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "is_generalized")
    private Boolean isGeneralized;

    @OneToMany(mappedBy = "logicalGroup")
    private Set<LogicalGroupLink> logicalGroupLinks = new LinkedHashSet<>();

    public ScoringLogicalGroup(String name, Boolean isGeneralized) {
        this.name = name;
        this.isGeneralized = isGeneralized;
    }

    public ScoringLogicalGroup() {
    }


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

    @Override
    public String toString() {
        return "ScoringLogicalGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isGeneralized=" + isGeneralized +
                ", logicalGroupLinks=" + logicalGroupLinks +
                '}';
    }
}
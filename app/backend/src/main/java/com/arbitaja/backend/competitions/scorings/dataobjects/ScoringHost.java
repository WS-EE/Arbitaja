package com.arbitaja.backend.competitions.scorings.dataobjects;

import com.arbitaja.backend.agents.dataobjects.ScoringAgent;
import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "scoring_host")
public class ScoringHost {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "ip", length = Integer.MAX_VALUE)
    private String ip;

    @Column(name = "hostname", length = Integer.MAX_VALUE)
    private String hostname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scoring_group_id")
    private Scoring_groups_structure scoringGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_template_id")
    private ScoringHost hostTemplate;

    @Column(name = "is_template")
    private Boolean isTemplate;

    @Column(name = "is_generalized")
    private Boolean isGeneralized;

    @OneToMany(mappedBy = "scoringHost")
    private Set<LogicalGroupLink> logicalGroupLinks = new LinkedHashSet<>();

    @OneToMany(mappedBy = "scoringHost")
    private Set<ScoringAgent> scoringAgents = new LinkedHashSet<>();

    @OneToMany(mappedBy = "scoringHost")
    private Set<ScoringCriterion> scoringCriteria = new LinkedHashSet<>();

    @OneToMany(mappedBy = "scoringHost")
    private Set<ScoringHistory> scoringHistories = new LinkedHashSet<>();

    @OneToMany(mappedBy = "hostTemplate")
    private Set<ScoringHost> scoringHosts = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Scoring_groups_structure getScoringGroup() {
        return scoringGroup;
    }

    public void setScoringGroup(Scoring_groups_structure scoringGroup) {
        this.scoringGroup = scoringGroup;
    }

    public ScoringHost getHostTemplate() {
        return hostTemplate;
    }

    public void setHostTemplate(ScoringHost hostTemplate) {
        this.hostTemplate = hostTemplate;
    }

    public Boolean getIsTemplate() {
        return isTemplate;
    }

    public void setIsTemplate(Boolean isTemplate) {
        this.isTemplate = isTemplate;
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

    public Set<ScoringHost> getScoringHosts() {
        return scoringHosts;
    }

    public void setScoringHosts(Set<ScoringHost> scoringHosts) {
        this.scoringHosts = scoringHosts;
    }

}
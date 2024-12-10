package com.arbitaja.backend.agents.dataobjects;

import com.arbitaja.backend.competitions.dataobjects.Competition;
import com.arbitaja.backend.users.dataobjects.Api_token;
import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "agent_proxy")
public class AgentProxy {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "ip", length = Integer.MAX_VALUE)
    private String ip;

    @Column(name = "name", length = Integer.MAX_VALUE)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id")
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "token_id")
    private Api_token token;

    @Column(name = "authentication_type")
    private Integer authenticationType;

    @OneToMany(mappedBy = "agentProxy")
    private Set<ScoringAgent> scoringAgents = new LinkedHashSet<>();

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public Api_token getToken() {
        return token;
    }

    public void setToken(Api_token token) {
        this.token = token;
    }

    public Integer getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(Integer authenticationType) {
        this.authenticationType = authenticationType;
    }

    public Set<ScoringAgent> getScoringAgents() {
        return scoringAgents;
    }

    public void setScoringAgents(Set<ScoringAgent> scoringAgents) {
        this.scoringAgents = scoringAgents;
    }

}
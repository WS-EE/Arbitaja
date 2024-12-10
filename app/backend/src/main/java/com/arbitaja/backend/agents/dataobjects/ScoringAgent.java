package com.arbitaja.backend.agents.dataobjects;

import com.arbitaja.backend.competitions.scorings.dataobjects.ScoringCriterion;
import com.arbitaja.backend.competitions.scorings.dataobjects.ScoringHost;
import com.arbitaja.backend.users.dataobjects.Api_token;
import jakarta.persistence.*;

@Entity
@Table(name = "scoring_agent")
public class ScoringAgent {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scoring_host_id")
    private ScoringHost scoringHost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scoring_criteria_id")
    private ScoringCriterion scoringCriteria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scoring_agent_transport_id")
    private ScoringAgentTransport scoringAgentTransport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_id")
    private Script script;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "token_id")
    private Api_token token;

    @Column(name = "authentication_type")
    private Integer authenticationType;

    @Column(name = "agent_type")
    private Integer agentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_proxy_id")
    private AgentProxy agentProxy;

    @Column(name = "custom_api_endpoint", length = Integer.MAX_VALUE)
    private String customApiEndpoint;

    public ScoringAgent(ScoringHost scoringHost, ScoringCriterion scoringCriteria, ScoringAgentTransport scoringAgentTransport, Script script, Api_token token, Integer authenticationType, Integer agentType, AgentProxy agentProxy, String customApiEndpoint) {
        this.scoringHost = scoringHost;
        this.scoringCriteria = scoringCriteria;
        this.scoringAgentTransport = scoringAgentTransport;
        this.script = script;
        this.token = token;
        this.authenticationType = authenticationType;
        this.agentType = agentType;
        this.agentProxy = agentProxy;
        this.customApiEndpoint = customApiEndpoint;
    }

    public ScoringAgent() {
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

    public ScoringCriterion getScoringCriteria() {
        return scoringCriteria;
    }

    public void setScoringCriteria(ScoringCriterion scoringCriteria) {
        this.scoringCriteria = scoringCriteria;
    }

    public ScoringAgentTransport getScoringAgentTransport() {
        return scoringAgentTransport;
    }

    public void setScoringAgentTransport(ScoringAgentTransport scoringAgentTransport) {
        this.scoringAgentTransport = scoringAgentTransport;
    }

    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
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

    public Integer getAgentType() {
        return agentType;
    }

    public void setAgentType(Integer agentType) {
        this.agentType = agentType;
    }

    public AgentProxy getAgentProxy() {
        return agentProxy;
    }

    public void setAgentProxy(AgentProxy agentProxy) {
        this.agentProxy = agentProxy;
    }

    public String getCustomApiEndpoint() {
        return customApiEndpoint;
    }

    public void setCustomApiEndpoint(String customApiEndpoint) {
        this.customApiEndpoint = customApiEndpoint;
    }


}
package com.arbitaja.backend.agents.dataobjects;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "scoring_agent_transport")
public class ScoringAgentTransport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "tcp_port")
    private Integer tcpPort;

    @Column(name = "ssh_key")
    private String sshKey;

    @OneToMany(mappedBy = "scoringAgentTransport")
    private Set<ScoringAgent> scoringAgents = new LinkedHashSet<>();

    public ScoringAgentTransport(String name, Integer tcpPort, String sshKey) {
        this.name = name;
        this.tcpPort = tcpPort;
        this.sshKey = sshKey;
    }

    public ScoringAgentTransport() {
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

    public Integer getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(Integer tcpPort) {
        this.tcpPort = tcpPort;
    }

    public String getSshKey() {
        return sshKey;
    }

    public void setSshKey(String sshKey) {
        this.sshKey = sshKey;
    }

    public Set<ScoringAgent> getScoringAgents() {
        return scoringAgents;
    }

    public void setScoringAgents(Set<ScoringAgent> scoringAgents) {
        this.scoringAgents = scoringAgents;
    }

    @Override
    public String toString() {
        return "ScoringAgentTransport{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tcpPort=" + tcpPort +
                ", sshKey='" + sshKey + '\'' +
                ", scoringAgents=" + scoringAgents +
                '}';
    }
}
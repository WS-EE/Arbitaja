package com.arbitaja.backend.agents.dataobjects;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "script")
public class Script {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "script")
    private String script;

    @OneToMany(mappedBy = "script")
    private Set<ScoringAgent> scoringAgents = new LinkedHashSet<>();

    public Script(String name, String type, String script) {
        this.name = name;
        this.type = type;
        this.script = script;
    }

    public Script() {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Set<ScoringAgent> getScoringAgents() {
        return scoringAgents;
    }

    public void setScoringAgents(Set<ScoringAgent> scoringAgents) {
        this.scoringAgents = scoringAgents;
    }

    @Override
    public String toString() {
        return "Script{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", script='" + script + '\'' +
                ", scoringAgents=" + scoringAgents +
                '}';
    }
}
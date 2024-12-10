package com.arbitaja.backend.users.dataobjects;


import com.arbitaja.backend.agents.dataobjects.ScoringAgent;
import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;


@Entity
@Table(name = "api_token")
public class Api_token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "name")
    private String name;
    @Column(name = "token")
    private String token;
    @OneToMany(mappedBy = "token")
    private Set<ScoringAgent> scoringAgents = new LinkedHashSet<>();

    public Api_token(User user, String name, String token) {
        this.user = user;
        this.name = name;
        this.token = token;
    }

    public Api_token() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ScoringAgent> getScoringAgents() {
        return scoringAgents;
    }

    public void setScoringAgents(Set<ScoringAgent> scoringAgents) {
        this.scoringAgents = scoringAgents;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Api_token{" +
                "id=" + id +
                ", user=" + user +
                ", name='" + name + '\'' +
                ", token='" + token + '\'' +
                ", scoringAgents=" + scoringAgents +
                '}';
    }
}

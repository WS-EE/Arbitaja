package com.arbitaja.backend.competitors.dataobjects;


import com.arbitaja.backend.competitions.dataobjects.Competition;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "competitor_competition")
public class Competitor_competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "competitor_id")
    private Competitor competitor;

    @ManyToOne
    @JoinColumn(name = "competition_id")
    private Competition competition;

    public Competitor_competition(Competitor competitor, Competition competition) {
        this.competitor = competitor;
        this.competition = competition;
    }
    public Competitor_competition() {}

    @Override
    public String toString() {
        return "Competitor_competition{" +
                "id=" + id +
                ", competitor=" + competitor +
                ", competition=" + competition +
                '}';
    }
}

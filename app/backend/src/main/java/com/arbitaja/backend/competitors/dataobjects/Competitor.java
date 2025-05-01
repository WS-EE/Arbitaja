package com.arbitaja.backend.competitors.dataobjects;

import com.arbitaja.backend.competitions.scorings.dataobjects.Scoring_groups_structure;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "competitor")
public class Competitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "public_display_name_type")
    private int public_display_name_type;

    @Column(name = "alias")
    private String alias;

    @OneToOne
    @JoinColumn(name = "personal_data_id")
    private Personal_data personal_data;

    @OneToMany(mappedBy = "competitor", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Set<Competitor_competition> competitor_competitions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "competitor", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Set<Scoring_groups_structure> scoring_groups_structures = new LinkedHashSet<>();

    public Competitor(int public_display_name_type, Personal_data personal_data, String alias) {
        this.public_display_name_type = public_display_name_type;
        this.personal_data = personal_data;
        this.alias = alias;
    }
    public Competitor(Competitor competitor) {
        this.id = competitor.getId();
        this.public_display_name_type = competitor.getPublic_display_name_type();
        this.personal_data = competitor.getPersonal_data();
        this.alias = competitor.getAlias();
    }


    public Competitor() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getPublic_display_name_type() {
        return public_display_name_type;
    }

    public void setPublic_display_name_type(int public_display_name_type) {
        this.public_display_name_type = public_display_name_type;
    }

    public Personal_data getPersonal_data() {
        return personal_data;
    }

    public void setPersonal_data(Personal_data personal_data) {
        this.personal_data = personal_data;
    }

    public Set<Competitor_competition> getCompetitor_competitions() {
        return competitor_competitions;
    }

    public void setCompetitor_competitions(Set<Competitor_competition> competitor_competitions) {
        this.competitor_competitions = competitor_competitions;
    }

    public Set<Scoring_groups_structure> getScoring_groups_structures() {
        return scoring_groups_structures;
    }

    public void setScoring_groups_structures(Set<Scoring_groups_structure> scoring_groups_structures) {
        this.scoring_groups_structures = scoring_groups_structures;
    }

    @Override
    public String toString() {
        return "Competitor{" +
                "id=" + id +
                ", public_display_name_type=" + public_display_name_type +
                ", alias='" + alias + '\'' +
                ", personal_data=" + personal_data +
                '}';
    }
}

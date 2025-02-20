package com.arbitaja.backend.competitors.dataobjects;

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

    @OneToMany(mappedBy = "competitor")
    private Set<Competitor_competition> competitor_competitions = new LinkedHashSet<>();

    public Competitor(int public_display_name_type, Personal_data personal_data, String alias) {
        this.public_display_name_type = public_display_name_type;
        this.personal_data = personal_data;
        this.alias = alias;
    }


    public Competitor() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

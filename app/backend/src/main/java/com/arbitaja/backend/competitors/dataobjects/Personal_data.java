package com.arbitaja.backend.competitors.dataobjects;


import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "personal_data")
public class Personal_data {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "full_name")
    private String full_name;
    @Column(name = "email")
    private String email;
    @Column(name = "alias")
    private String alias;

    @ManyToOne
    @JoinColumn(name = "school_id", nullable = false)
    private School school;
    @Column(name = "created_at")
    private Timestamp created_at;

    public Personal_data(String full_name, String email, String alias, School school, Timestamp created_at) {
        this.full_name = full_name;
        this.email = email;
        this.alias = alias;
        this.school = school;
        this.created_at = created_at;
    }

    public Personal_data() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "Personal_data{" +
                "id=" + id +
                ", full_name='" + full_name + '\'' +
                ", email='" + email + '\'' +
                ", alias='" + alias + '\'' +
                ", school=" + school +
                ", created_at=" + created_at +
                '}';
    }
}

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

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;
    @Column(name = "created_at")
    private Timestamp created_at;

    public Personal_data(String full_name, String email, School school, Timestamp created_at) {
        this.full_name = full_name;
        this.email = email;
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
                ", school=" + school +
                ", created_at=" + created_at +
                '}';
    }
}

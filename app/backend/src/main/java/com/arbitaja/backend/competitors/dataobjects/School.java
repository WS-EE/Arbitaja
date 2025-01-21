package com.arbitaja.backend.competitors.dataobjects;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "school")
public class School {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "created_at")
    @Schema(hidden = true)
    private Timestamp created_at;

    @OneToMany(mappedBy = "school")
    private Set<Personal_data> personalData = new LinkedHashSet<>();

    public School(String name, Timestamp created_at) {
        this.name = name;
        this.created_at = created_at;
    }


    public School() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "School{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", created_at=" + created_at +
                '}';
    }
}

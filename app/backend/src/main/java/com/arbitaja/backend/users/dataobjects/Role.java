package com.arbitaja.backend.users.dataobjects;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;


import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;
    @Schema(hidden = true)
    @Column(name = "created_at")
    private Timestamp created_at;
    @Schema(hidden = true)
    @Column(name = "changed_at")
    private Timestamp changed_at;

    @OneToMany(mappedBy = "parentRole", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Role_relation> parentRoleRelations = new LinkedHashSet<>();

    @OneToMany(mappedBy = "childRole", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Role_relation> childRoleRelations = new LinkedHashSet<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Role_permissions> rolePermissions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<User_role> userRoles = new LinkedHashSet<>();



    public Role(String name, Timestamp created_at, Timestamp changed_at) {
        this.name = name;
        this.created_at = created_at;
        this.changed_at = changed_at;
    }

    public Role(String name) {
        this.name = name;
        this.created_at = Timestamp.from(Instant.now());
        this.changed_at = Timestamp.from(Instant.now());
    }

    public Role() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public Timestamp getChanged_at() {
        return changed_at;
    }

    public void setChanged_at(Timestamp changed_at) {
        this.changed_at = changed_at;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", created_at=" + created_at +
                ", changed_at=" + changed_at +
                '}';
    }
}

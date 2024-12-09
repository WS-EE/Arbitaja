package com.arbitaja.backend.users.dataobjects;

import jakarta.persistence.*;


import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;
    @Column(name = "created_at")
    private Timestamp created_at;
    @Column(name = "changed_at")
    private Timestamp changed_at;

    @OneToMany(mappedBy = "role")
    private List<Role_permissions> rolePermissions;
    @OneToMany(mappedBy = "role")
    private List<User_role> userRoles;



    public Role(int id, String name, Timestamp created_at, Timestamp changed_at) {
        this.id = id;
        this.name = name;
        this.created_at = created_at;
        this.changed_at = changed_at;
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
                ", rolePermissions=" + rolePermissions +
                ", userRoles=" + userRoles +
                '}';
    }
}

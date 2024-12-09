package com.arbitaja.backend.users.dataobjects;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "user_role")
public class User_role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "created_at")
    private Timestamp created_at;


    public User_role(int id, User user, Role role, Timestamp created_at) {
        this.id = id;
        this.user = user;
        this.role = role;
        this.created_at = created_at;
    }

    public User_role() {}

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "User_role{" +
                "id=" + id +
                ", user=" + user +
                ", role=" + role +
                ", created_at=" + created_at +
                '}';
    }
}

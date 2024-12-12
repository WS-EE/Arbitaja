package com.arbitaja.backend.users.dataobjects;


import jakarta.persistence.*;

@Entity
@Table(name = "role_relation")
public class Role_relation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @ManyToOne
    @JoinColumn(name = "parent_role_id")
    private Role parentRole;
    @ManyToOne
    @JoinColumn(name = "child_role_id")
    private Role childRole;

    public Role_relation( Role parent_role, Role child_role) {
        this.parentRole = parent_role;
        this.childRole = child_role;
    }
    public Role_relation() {}


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Role getParentRole() {
        return parentRole;
    }

    public void setParentRole(Role parent_role) {
        this.parentRole = parent_role;
    }

    public Role getChildRole() {
        return childRole;
    }

    public void setChildRole(Role childRole) {
        this.childRole = childRole;
    }

    @Override
    public String toString() {
        return "Role_relation{" +
                "id=" + id +
                ", parent_role=" + parentRole +
                ", child_role=" + childRole +
                '}';
    }
}

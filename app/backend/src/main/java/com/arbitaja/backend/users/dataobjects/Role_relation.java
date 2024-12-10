package com.arbitaja.backend.users.dataobjects;


import jakarta.persistence.*;

@Entity
@Table(name = "role_relation")
public class Role_relation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    @ManyToOne
    @JoinColumn(name = "parent_role_id")
    private Role parent_role;
    @ManyToOne
    @JoinColumn(name = "child_role_id")
    private Role child_role;

    public Role_relation(Role role, Role parent_role, Role child_role) {
        this.role = role;
        this.parent_role = parent_role;
        this.child_role = child_role;
    }
    public Role_relation() {}

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Role getParent_role() {
        return parent_role;
    }

    public void setParent_role(Role parent_role) {
        this.parent_role = parent_role;
    }

    public Role getChild_role() {
        return child_role;
    }

    public void setChild_role(Role child_role) {
        this.child_role = child_role;
    }

    @Override
    public String toString() {
        return "Role_relation{" +
                "id=" + id +
                ", role=" + role +
                ", parent_role=" + parent_role +
                ", child_role=" + child_role +
                '}';
    }
}

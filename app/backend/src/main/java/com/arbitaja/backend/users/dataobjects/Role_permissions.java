package com.arbitaja.backend.users.dataobjects;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.util.Map;


@Entity
@Table(name = "role_permissions")
public class Role_permissions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    @Type(JsonBinaryType.class)
    @Column(name = "key_object_acl", columnDefinition = "jsonb")
    private Map<String, String> key_object_acl;

    public Role_permissions(Permission permission, Role role, Map<String, String> key_object_acl) {
        this.permission = permission;
        this.role = role;
        this.key_object_acl = key_object_acl;
    }

    public Role_permissions() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Permission getPermissions() {
        return permission;
    }

    public void setPermissions(Permission permission) {
        this.permission = permission;
    }


    public Role getRoles() {
        return role;
    }

    public void setRoles(Role roles) {
        this.role = roles;
    }

    public Map<String, String> getKey_object_acl() {
        return key_object_acl;
    }

    public void setKey_object_acl(Map<String, String> key_object_acl) {
        this.key_object_acl = key_object_acl;
    }

    @Override
    public String toString() {
        return "Role_permissions{" +
                "id=" + id +
                ", permission=" + permission +
                ", role=" + role +
                ", key_object_acl=" + key_object_acl +
                '}';
    }
}

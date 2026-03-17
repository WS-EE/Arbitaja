package com.arbitaja.backend.users.dataobjects;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "permission")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;
    @Column(name = "key")
    private String key;
    @Column(name = "key_object")
    private String key_object;

    @OneToMany(mappedBy = "permission", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Role_permissions> rolePermissions = new LinkedHashSet<>();

    public Permission(String name, String key, String key_object) {
        this.name = name;
        this.key = key;
        this.key_object = key_object;
    }

    public Permission() {
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", key='" + key + '\'' +
                ", key_object='" + key_object + '\'' +
                '}';
    }
}

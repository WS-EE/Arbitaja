package com.arbitaja.backend.users.dataobjects;

import com.arbitaja.backend.competitors.dataobjects.Personal_data;
import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "\"user\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "default_token_id")
    private Api_token default_token;

    @Column(name = "username")
    private String username;
    @Column(name = "salted_password")
    private String salted_password;
    @OneToOne
    @JoinColumn(name = "personal_data_id")
    private Personal_data personal_data;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Api_token> apiTokens = new LinkedHashSet<>();

    public User(Personal_data personal_data, String salted_password, String username, Api_token default_token) {
        this.personal_data = personal_data;
        this.salted_password = salted_password;
        this.username = username;
        this.default_token = default_token;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Api_token getDefault_token() {
        return default_token;
    }

    public void setDefault_token(Api_token default_token) {
        this.default_token = default_token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSalted_password() {
        return salted_password;
    }

    public void setSalted_password(String salted_password) {
        this.salted_password = salted_password;
    }

    public Personal_data getPersonal_data() {
        return personal_data;
    }

    public void setPersonal_data(Personal_data personal_data) {
        this.personal_data = personal_data;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", default_token=" + default_token +
                ", username='" + username + '\'' +
                ", salted_password='" + salted_password + '\'' +
                ", personal_data=" + personal_data +
                ", apiTokens=" + apiTokens +
                '}';
    }
}

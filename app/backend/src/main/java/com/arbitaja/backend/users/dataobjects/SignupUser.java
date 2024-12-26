package com.arbitaja.backend.users.dataobjects;

import com.arbitaja.backend.competitors.dataobjects.Personal_data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "signup_user")
public class SignupUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "username", length = Integer.MAX_VALUE, unique = true, nullable = false)
    private String username;

    @JsonIgnore
    @Column(name = "salted_password", length = Integer.MAX_VALUE)
    private String salted_password;

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "personal_data_id", nullable = false)
    private Personal_data personal_data;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved = false;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public void setSalted_password(String saltedPassword) {
        this.salted_password = saltedPassword;
    }

    public Personal_data getPersonal_data() {
        return personal_data;
    }

    public void setPersonal_data(Personal_data personalData) {
        this.personal_data = personalData;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "SignupUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", saltedPassword='" + salted_password + '\'' +
                ", personalData=" + personal_data +
                ", isApproved=" + isApproved +
                ", createdAt=" + createdAt +
                '}';
    }
}
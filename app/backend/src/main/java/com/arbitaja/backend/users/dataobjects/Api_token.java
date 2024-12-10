package com.arbitaja.backend.users.dataobjects;


import jakarta.persistence.*;


@Entity
@Table(name = "api_token")
public class Api_token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "name")
    private String name;
    @Column(name = "token")
    private String token;
}

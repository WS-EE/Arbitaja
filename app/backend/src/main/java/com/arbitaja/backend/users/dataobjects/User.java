package com.arbitaja.backend.users.dataobjects;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int default_token_id; // to be changed

    @Column(name = "username")
    private String username;
    @Column(name = "salted_password")
    private String salted_password;
    private int personal_data_id; // to be changed

}

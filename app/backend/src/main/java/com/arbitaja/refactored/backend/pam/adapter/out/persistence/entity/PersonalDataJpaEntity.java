package com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

/**
 * JPA Entity for PersonalData persistence.
 */
@Entity
@Table(name = "personal_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class PersonalDataJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "full_name")
    @NonNull
    private String fullName;

    @Column(name = "email")
    @NonNull
    private String email;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private SchoolJpaEntity school;

    @Column(name = "created_at")
    private Timestamp createdAt;
}


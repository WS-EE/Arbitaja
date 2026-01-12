package com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA Entity for School persistence.
 */
@Entity
@Table(name = "school")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class SchoolJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    @NonNull
    private String name;
}


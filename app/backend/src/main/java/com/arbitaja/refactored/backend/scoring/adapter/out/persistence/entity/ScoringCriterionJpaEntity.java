package com.arbitaja.refactored.backend.scoring.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

/**
 * JPA entity for scoring criterion persistence.
 *
 * <p>Maps the {@code scoring_criteria} table without referencing the related
 * scoring host or template entities so the scoring module is decoupled from those
 * legacy aggregates.</p>
 */
@Entity
@Table(name = "scoring_criteria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoringCriterionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_manual")
    private Boolean manual;

    @Column(name = "total_points")
    private Double totalPoints;

    @Column(name = "is_generalized")
    private Boolean generalized;

    @Column(name = "expected_result")
    private String expectedResult;

    @Column(name = "is_template")
    private Boolean template;

    @Column(name = "visibility_level")
    private Integer visibilityLevel;

    @Column(name = "scoring_host_id")
    private Integer scoringHostId;

    @Column(name = "criteria_template_id")
    private Integer criteriaTemplateId;

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass = o instanceof HibernateProxy
            ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
            : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
            : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        ScoringCriterionJpaEntity that = (ScoringCriterionJpaEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
            : getClass().hashCode();
    }
}

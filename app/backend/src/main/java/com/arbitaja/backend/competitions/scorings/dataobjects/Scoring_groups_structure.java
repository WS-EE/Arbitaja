package com.arbitaja.backend.competitions.scorings.dataobjects;


import com.arbitaja.backend.competitors.dataobjects.Competitor;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLCITextType;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "scoring_groups_structure")
public class Scoring_groups_structure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Type(PostgreSQLCITextType.class)
    @Column(name = "description", columnDefinition = "text")
    private String description;
    @ManyToOne
    @JoinColumn(name = "competitor_id")
    private Competitor competitor;
    @ManyToOne
    @JoinColumn(name = "scoring_parent_group_id", referencedColumnName = "id")
    private Scoring_groups_structure parent_scoring_groups_structure;
    @OneToMany(mappedBy = "parent_scoring_groups_structure")
    private Set<Scoring_groups_structure> childGroups = new LinkedHashSet<>();
    @Column(name = "structure_group_type")
    private int structure_group_type;
    @Type(JsonBinaryType.class)
    @Column(name = "dynamic_variables", columnDefinition = "jsonb")
    private Map<String, String> dynamic_variables;


    public Scoring_groups_structure(String name, String description, Competitor competitor, Scoring_groups_structure parent_scoring_groups_structure, int structure_group_type, Map<String, String> dynamic_variables) {
        this.name = name;
        this.description = description;
        this.competitor = competitor;
        this.parent_scoring_groups_structure = parent_scoring_groups_structure;
        this.structure_group_type = structure_group_type;
        this.dynamic_variables = dynamic_variables;
    }


    public Scoring_groups_structure() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Competitor getCompetitor() {
        return competitor;
    }

    public void setCompetitor(Competitor competitor) {
        this.competitor = competitor;
    }

    public Scoring_groups_structure getParent_scoring_groups_structure() {
        return parent_scoring_groups_structure;
    }

    public void setParent_scoring_groups_structure(Scoring_groups_structure parent_scoring_groups_structure) {
        this.parent_scoring_groups_structure = parent_scoring_groups_structure;
    }

    public Map<String, String> getDynamic_variables() {
        return dynamic_variables;
    }

    public void setDynamic_variables(Map<String, String> dynamic_variables) {
        this.dynamic_variables = dynamic_variables;
    }

    public int getStructure_group_type() {
        return structure_group_type;
    }

    public void setStructure_group_type(int structure_group_type) {
        this.structure_group_type = structure_group_type;
    }

    @Override
    public String toString() {
        return "Scoring_groups_structure{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", competitor=" + competitor +
                ", parent_scoring_groups_structure=" + parent_scoring_groups_structure +
                ", structure_group_type=" + structure_group_type +
                ", dynamic_variables=" + dynamic_variables +
                '}';
    }
}

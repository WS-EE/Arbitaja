package com.arbitaja.refactored.backend.scoring.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Slim competitor projection used by scoring use cases. Resolves the public alias
 * based on {@link PublicDisplayNameType} so callers do not need to know about the
 * underlying personal data model.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ScoringCompetitor {

    private Integer id;
    private String alias;
    private String fullName;
    private String schoolName;
    private Integer publicDisplayNameType;

    public String resolvedDisplayName() {
        PublicDisplayNameType type = PublicDisplayNameType.fromCode(publicDisplayNameType);
        return switch (type) {
            case FULL_NAME -> fullName;
            case SCHOOL -> schoolName;
            case ALIAS -> alias;
        };
    }
}

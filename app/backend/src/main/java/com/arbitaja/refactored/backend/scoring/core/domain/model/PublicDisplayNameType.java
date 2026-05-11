package com.arbitaja.refactored.backend.scoring.core.domain.model;

import lombok.Getter;

/**
 * Mirrors the legacy {@code competitor.public_display_name_type} integer column.
 */
@Getter
public enum PublicDisplayNameType {

    FULL_NAME(1),
    SCHOOL(2),
    ALIAS(3);

    private final int code;

    PublicDisplayNameType(int code) {
        this.code = code;
    }

    public static PublicDisplayNameType fromCode(Integer code) {
        if (code == null) {
            throw new IllegalStateException("Unexpected value for public display name: null");
        }
        for (PublicDisplayNameType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalStateException("Unexpected value for public display name: " + code);
    }
}

package com.gbsw.template.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    STUDENT,
    ADMIN;

    @JsonValue
    public String toLower() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static Role from(String value) {
        if (value == null) {
            return null;
        }
        return Role.valueOf(value.toUpperCase());
    }
}

package com.gbsw.template.domain.check.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CheckStatus {
    CHECKED,
    UNCHECKED;

    @JsonValue
    public String toLower() {
        return name().toLowerCase();
    }
}

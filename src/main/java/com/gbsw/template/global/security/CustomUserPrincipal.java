package com.gbsw.template.global.security;

import com.gbsw.template.domain.user.entity.Role;
import lombok.Getter;

@Getter
public class CustomUserPrincipal {

    private final Long id;
    private final Role role;

    public CustomUserPrincipal(Long id, Role role) {
        this.id = id;
        this.role = role;
    }
}

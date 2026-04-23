package com.gbsw.template.global.security;

import com.gbsw.template.global.exception.CustomException;
import com.gbsw.template.global.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthUtil {

    private AuthUtil() {
    }

    public static CustomUserPrincipal currentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserPrincipal principal)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return principal;
    }

    public static void ensureSelf(Long requestedId) {
        CustomUserPrincipal principal = currentPrincipal();
        if (!principal.getId().equals(requestedId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }
}

package com.gbsw.template.global.security.jwt;

import com.gbsw.template.domain.user.entity.Role;
import com.gbsw.template.global.exception.CustomException;
import com.gbsw.template.global.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

import com.gbsw.template.global.security.CustomUserPrincipal;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private static final String CLAIM_TYPE = "type";
    private static final String CLAIM_ROLE = "role";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    public String generateAccessToken(Long userId, Role role) {
        return generateToken(userId, role, jwtProperties.getAccessTokenExpiration(), TYPE_ACCESS);
    }

    public String generateRefreshToken(Long userId, Role role) {
        return generateToken(userId, role, jwtProperties.getRefreshTokenExpiration(), TYPE_REFRESH);
    }

    private String generateToken(Long userId, Role role, long expiration, String type) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim(CLAIM_TYPE, type)
                .claim(CLAIM_ROLE, role.name())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isRefreshToken(String token) {
        return TYPE_REFRESH.equals(parseClaims(token).get(CLAIM_TYPE, String.class));
    }

    public Long getUserIdFromToken(String token) {
        try {
            return Long.parseLong(parseClaims(token).getSubject());
        } catch (NumberFormatException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    public Role getRoleFromToken(String token) {
        String roleValue = parseClaims(token).get(CLAIM_ROLE, String.class);
        if (roleValue == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        try {
            return Role.valueOf(roleValue);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    public Authentication getAuthentication(String token) {
        Long userId = getUserIdFromToken(token);
        Role role = getRoleFromToken(token);
        CustomUserPrincipal principal = new CustomUserPrincipal(userId, role);
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
        );
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(ErrorCode.UNSUPPORTED_TOKEN);
        } catch (MalformedJwtException | SecurityException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.EMPTY_TOKEN);
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

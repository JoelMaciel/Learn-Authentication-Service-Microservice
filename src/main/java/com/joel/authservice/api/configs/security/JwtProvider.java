package com.joel.authservice.api.configs.security;

import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

@Log4j2
@Component
public class JwtProvider {

    @Value("${learn.auth.jwtSecret}")
    private String jwtSecret;

    @Value("${learn.auth.jwtExpirationMs}")
    private String jwtExpirationMs;

    public String generateJwt(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        final String roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        long expirationTimeMs;
        expirationTimeMs = getExpirationTimeMs();

        return Jwts.builder()
                .setSubject(userPrincipal.getUserId().toString())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusMillis(expirationTimeMs)))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getSubjectJwt(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwt(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {} ", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {} ", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {} ", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {} ", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {} ", e.getMessage());
        }
        return false;
    }
    private long getExpirationTimeMs() {
        long expirationTimeMs;
        try {
            expirationTimeMs = Long.parseLong(jwtExpirationMs);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid format for JWT expiration time", e);
        }
        return expirationTimeMs;
    }
}

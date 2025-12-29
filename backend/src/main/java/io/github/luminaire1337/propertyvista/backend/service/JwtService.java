package io.github.luminaire1337.propertyvista.backend.service;

import io.github.luminaire1337.propertyvista.backend.entity.User;
import io.github.luminaire1337.propertyvista.backend.exception.BadRequestException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Slf4j
@Service
public class JwtService {
    @Value("${PROPERTYVISTA_JWT_SECRET}")
    private String secret;

    @Getter
    @Value("${PROPERTYVISTA_JWT_EXPIRATION_MS}")
    private Long expirationMs;

    private SecretKey getSigningKey() {
        if (secret.length() < 32) {
            log.warn("JWT secret is less than 32 characters long, using a random key instead.");
            return Jwts.SIG.HS256.key().build();
        }
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("roles", List.of(user.getRole().name()));

        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(user.getId().toString())
                .claims(claims)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extractPayload(String token) {
        try {
            var claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            var expiration = claims.getExpiration();
            if (!expiration.after(new Date())) {
                throw new RuntimeException("Token wygasł");
            }

            return claims;
        } catch (Exception e) {
            throw new BadRequestException("Nieprawidłowy token dostępu");
        }
    }

    public UUID extractUserIdFromClaims(Claims claims) {
        return UUID.fromString(claims.getSubject());
    }

    public String extractEmailFromClaims(Claims claims) {
        return claims.get("email", String.class);
    }

    public List<String> extractRolesFromClaims(Claims claims) {
        Object rawRoles = claims.get("roles");

        if (rawRoles instanceof List<?> roles) {
            return roles.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .toList();
        }

        return Collections.emptyList();
    }
}

package io.github.luminaire1337.propertyvista.backend.service;

import io.github.luminaire1337.propertyvista.backend.exception.InvalidAccessTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class JwtService {
    @Value("${propertyvista.jwt.secret}")
    private String secret;

    @Getter
    @Value("${propertyvista.jwt.expiration-ms}")
    private Long expirationMs;

    private SecretKey getSigningKey() {
        if (secret.length() < 32) {
            log.warn("JWT secret is less than 32 characters long, using a random key instead.");
            return Jwts.SIG.HS256.key().build();
        }
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateAccessToken(UUID userId) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    private Claims extractPayload(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new InvalidAccessTokenException("Invalid or expired access token");
        }
    }

    public UUID extractUserId(String token) {
        Claims claims = extractPayload(token);
        return UUID.fromString(claims.getSubject());
    }

    public void ensureTokenValid(String token) {
        Claims claims = extractPayload(token);
        Date expiration = claims.getExpiration();
        if (!expiration.after(new Date())) {
            throw new InvalidAccessTokenException("Access token has expired");
        }
    }
}

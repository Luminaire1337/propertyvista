package io.github.luminaire1337.propertyvista.backend.identity.auth.service;

import io.github.luminaire1337.propertyvista.backend.identity.auth.exception.InvalidAccessTokenException;
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

@Service
@Slf4j
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

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractPayload(token);
            Date expiration = claims.getExpiration();
            return expiration.after(new Date());
        } catch (InvalidAccessTokenException e) {
            return false;
        }
    }
}

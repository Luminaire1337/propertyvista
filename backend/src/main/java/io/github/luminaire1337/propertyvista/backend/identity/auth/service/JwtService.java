package io.github.luminaire1337.propertyvista.backend.identity.auth;

import io.github.luminaire1337.propertyvista.backend.identity.auth.exception.InvalidAccessTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    @Value("${propertyvista.jwt.secret}")
    private String secret;

    @Getter
    @Value("${propertyvista.jwt.expiration-ms}")
    private Long expirationMs;

    private SecretKey getSigningKey() {
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

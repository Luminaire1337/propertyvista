package io.github.luminaire1337.propertyvista.backend.identity.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Long expirationMs
) {
}

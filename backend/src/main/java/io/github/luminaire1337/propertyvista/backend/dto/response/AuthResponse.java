package io.github.luminaire1337.propertyvista.backend.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Long expirationMs
) {
}

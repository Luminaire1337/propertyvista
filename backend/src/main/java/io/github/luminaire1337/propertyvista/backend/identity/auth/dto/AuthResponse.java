package io.github.luminaire1337.propertyvista.backend.identity.auth.dto;

import java.util.UUID;

public record AuthResponse(
        String accessToken,
        UUID refreshToken,
        Long expirationMs
) {
}

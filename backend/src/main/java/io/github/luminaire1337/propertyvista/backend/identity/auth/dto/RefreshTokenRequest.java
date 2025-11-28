package io.github.luminaire1337.propertyvista.backend.identity.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token must not be blank")
        String refreshToken
) {
}

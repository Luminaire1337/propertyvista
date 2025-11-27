package io.github.luminaire1337.propertyvista.backend.identity.auth.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.UUID;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token must not be blank")
        @UUID(message = "Refresh token must be a valid UUID")
        String refreshToken
) {
}

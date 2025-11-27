package io.github.luminaire1337.propertyvista.backend.identity.auth.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.UUID;

public record LogoutRequest(
        @NotBlank(message = "Refresh token must not be blank")
        @UUID(message = "Refresh token must be a valid UUID")
        java.util.UUID refreshToken
) {
}

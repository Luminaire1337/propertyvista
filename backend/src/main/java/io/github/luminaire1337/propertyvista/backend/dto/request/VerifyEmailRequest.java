package io.github.luminaire1337.propertyvista.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyEmailRequest(
        @NotBlank(message = "Verification token must not be blank")
        @Size(min = 1, max = 255, message = "Verification token must be between 1 and 255 characters long")
        String token
) {
}

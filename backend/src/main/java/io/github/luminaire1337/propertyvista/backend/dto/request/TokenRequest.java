package io.github.luminaire1337.propertyvista.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TokenRequest(
        @NotBlank(message = "Token must not be blank")
        @Size(max = 255, message = "Token must not exceed 255 characters")
        String token
) {
}

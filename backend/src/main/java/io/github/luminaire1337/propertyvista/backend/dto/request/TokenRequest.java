package io.github.luminaire1337.propertyvista.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TokenRequest(
        @NotBlank(message = "Token jest wymagany")
        @Size(max = 255, message = "Token nie może przekraczać 255 znaków")
        String token
) {
}

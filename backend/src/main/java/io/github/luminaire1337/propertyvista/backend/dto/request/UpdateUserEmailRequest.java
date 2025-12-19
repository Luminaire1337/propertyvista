package io.github.luminaire1337.propertyvista.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserEmailRequest(
        @NotBlank(message = "Adres e-mail jest wymagany")
        @Size(min = 3, max = 64, message = "Adres e-mail musi mieć od 3 do 64 znaków")
        @Email(message = "Nieprawidłowy format adresu e-mail")
        String email
) {
}

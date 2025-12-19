package io.github.luminaire1337.propertyvista.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Adres e-mail jest wymagany")
        @Size(min = 3, max = 64, message = "Adres e-mail musi mieć od 3 do 64 znaków")
        @Email(message = "Nieprawidłowy format adresu e-mail")
        String email,
        @NotBlank(message = "Hasło jest wymagane")
        @Size(min = 8, max = 64, message = "Hasło musi mieć od 8 do 64 znaków")
        String password
) {
}

package io.github.luminaire1337.propertyvista.backend.dto.request;

import io.github.luminaire1337.propertyvista.backend.helper.CommonRegExps;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserInfoRequest(
        @NotBlank(message = "Imię jest wymagane")
        @Size(min = 3, max = 50, message = "Imię musi być między 3 a 50 znaków")
        String firstName,
        @NotBlank(message = "Nazwisko jest wymagane")
        @Size(min = 3, max = 50, message = "Nazwisko musi być między 3 a 50 znaków")
        String lastName,
        @NotBlank(message = "Numer telefonu jest wymagany")
        @Size(min = 7, max = 15, message = "Numer telefonu musi mieć od 7 do 15 znaków")
        @Pattern(regexp = CommonRegExps.phoneNumber, message = "Nieprawidłowy format numeru telefonu")
        String phoneNumber
) {
}

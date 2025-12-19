package io.github.luminaire1337.propertyvista.backend.dto.request;

import io.github.luminaire1337.propertyvista.backend.helper.CommonRegExps;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Adres e-mail jest wymagany")
        @Size(min = 3, max = 64, message = "Adres e-mail musi mieć od 3 do 64 znaków")
        @Email(message = "Nieprawidłowy format adresu e-mail")
        String email,
        @NotBlank(message = "Hasło jest wymagane")
        @Size(min = 8, max = 64, message = "Hasło musi mieć od 8 do 64 znaków")
        @Pattern(regexp = CommonRegExps.password, message = "Hasło musi zawierać co najmniej jedną wielką literę, jedną małą literę, jedną cyfrę oraz jeden znak specjalny")
        String password,
        @NotBlank(message = "Imię jest wymagane")
        @Size(min = 3, max = 50, message = "Imię musi mieć między 3 a 50 znaków")
        String firstName,
        @NotBlank(message = "Nazwisko jest wymagane")
        @Size(min = 3, max = 50, message = "Nazwisko musi mieć między 3 a 50 znaków")
        String lastName,
        @NotBlank(message = "Numer telefonu jest wymagany")
        @Size(min = 7, max = 15, message = "Numer telefonu musi mieć od 7 do 15 znaków")
        @Pattern(regexp = CommonRegExps.phoneNumber, message = "Nieprawidłowy format numeru telefonu")
        String phoneNumber
) {

}

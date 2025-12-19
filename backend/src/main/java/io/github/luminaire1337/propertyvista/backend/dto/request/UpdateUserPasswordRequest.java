package io.github.luminaire1337.propertyvista.backend.dto.request;

import io.github.luminaire1337.propertyvista.backend.helper.CommonRegExps;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserPasswordRequest(
        @NotBlank(message = "Hasło jest wymagane")
        @Size(min = 8, max = 64, message = "Hasło musi mieć od 8 do 64 znaków")
        @Pattern(regexp = CommonRegExps.password, message = "Hasło musi zawierać co najmniej jedną wielką literę, jedną małą literę, jedną cyfrę oraz jeden znak specjalny")
        String password
) {
}

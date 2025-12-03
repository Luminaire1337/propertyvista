package io.github.luminaire1337.propertyvista.backend.dto.request;

import io.github.luminaire1337.propertyvista.backend.vo.SafePassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Email is required")
        @Size(min = 3, max = 64, message = "Email must be between 3 and 64 characters")
        @Email(message = "Invalid email format")
        String email,
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
        @Pattern(regexp = SafePassword.regexp, message = SafePassword.message)
        String password
) {
}

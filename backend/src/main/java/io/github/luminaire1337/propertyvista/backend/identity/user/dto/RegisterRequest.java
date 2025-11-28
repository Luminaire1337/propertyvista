package io.github.luminaire1337.propertyvista.backend.identity.user.dto;

import io.github.luminaire1337.propertyvista.backend.shared.EmailAddress;
import io.github.luminaire1337.propertyvista.backend.shared.PhoneNumber;
import io.github.luminaire1337.propertyvista.backend.shared.SafePassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Email is required")
        @Size(min = 3, max = 64, message = "Email must be between 3 and 64 characters")
        @Email(message = EmailAddress.message)
        String email,
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
        @Pattern(regexp = SafePassword.regexp, message = SafePassword.message)
        String password,
        @NotBlank(message = "First name is required")
        @Size(min = 3, max = 50, message = "First name must be between 3 and 50 characters")
        String firstName,
        @NotBlank(message = "Last name is required")
        @Size(min = 3, max = 50, message = "Last name must be between 3 and 50 characters")
        String lastName,
        @NotBlank(message = "Phone number is required")
        @Size(min = 7, max = 15, message = "Phone number must be between 7 and 15 characters")
        @Pattern(regexp = PhoneNumber.regexp, message = PhoneNumber.message)
        String phoneNumber
) {

}

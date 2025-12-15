package io.github.luminaire1337.propertyvista.backend.dto.request;

import io.github.luminaire1337.propertyvista.backend.helper.CommonRegExps;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserInfoRequest(
        @NotBlank(message = "First name is required")
        @Size(min = 3, max = 50, message = "First name must be between 3 and 50 characters")
        String firstName,
        @NotBlank(message = "Last name is required")
        @Size(min = 3, max = 50, message = "Last name must be between 3 and 50 characters")
        String lastName,
        @NotBlank(message = "Phone number is required")
        @Size(min = 7, max = 15, message = "Phone number must be between 7 and 15 characters")
        @Pattern(regexp = CommonRegExps.phoneNumber, message = "Your phone number must be in E.164 format, e.g., +1234567890")
        String phoneNumber
) {
}

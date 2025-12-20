package io.github.luminaire1337.propertyvista.backend.dto.request;

import io.github.luminaire1337.propertyvista.backend.entity.utility.UserRole;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequest(
        @NotNull(message = "Rola użytkownika jest wymagana")
        UserRole role
) {
}

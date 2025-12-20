package io.github.luminaire1337.propertyvista.backend.dto.request;

import io.github.luminaire1337.propertyvista.backend.entity.utility.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(
        @NotNull(message = "Status użytkownika jest wymagany")
        UserStatus status
) {
}

package io.github.luminaire1337.propertyvista.backend.dto.response;

import java.util.UUID;

public record UserPropertyDetailedResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        String avatarImagePath
) {
}

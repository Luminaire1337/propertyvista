package io.github.luminaire1337.propertyvista.backend.dto.response;

import io.github.luminaire1337.propertyvista.backend.entity.UserRole;
import io.github.luminaire1337.propertyvista.backend.entity.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        UserRole role,
        UserStatus status,
        String firstName,
        String lastName,
        String phoneNumber,
        String avatarImagePath,
        Integer propertyPoints,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

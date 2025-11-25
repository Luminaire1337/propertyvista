package io.github.luminaire1337.propertyvista.backend.identity.user.dto;

import io.github.luminaire1337.propertyvista.backend.identity.user.UserRole;
import io.github.luminaire1337.propertyvista.backend.identity.user.UserStatus;
import io.github.luminaire1337.propertyvista.backend.shared.EmailAddress;
import io.github.luminaire1337.propertyvista.backend.shared.ImagePath;
import io.github.luminaire1337.propertyvista.backend.shared.PhoneNumber;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        EmailAddress email,
        UserRole role,
        UserStatus status,
        String firstName,
        String lastName,
        PhoneNumber phoneNumber,
        ImagePath avatarImagePath,
        Integer propertyPoints,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

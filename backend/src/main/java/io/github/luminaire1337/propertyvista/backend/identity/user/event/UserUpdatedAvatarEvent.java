package io.github.luminaire1337.propertyvista.backend.identity.user.event;

import io.github.luminaire1337.propertyvista.backend.shared.ImagePath;

import java.util.UUID;

public record UserUpdatedAvatarEvent(UUID userId, ImagePath avatarPath) {
}

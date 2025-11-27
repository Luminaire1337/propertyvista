package io.github.luminaire1337.propertyvista.backend.identity.user.event;

import io.github.luminaire1337.propertyvista.backend.notification.NotifiableEvent;

import java.util.UUID;

public record UserCreatedEvent(UUID userId, UUID verificationToken) implements NotifiableEvent {
}
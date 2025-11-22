package io.github.luminaire1337.propertyvista.backend.identity.event;

import io.github.luminaire1337.propertyvista.backend.identity.user.User;
import io.github.luminaire1337.propertyvista.backend.notification.NotifiableEvent;

public record UserDeletedEvent(User user) implements NotifiableEvent {}
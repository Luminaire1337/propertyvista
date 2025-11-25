package io.github.luminaire1337.propertyvista.backend.identity.user.event;

import io.github.luminaire1337.propertyvista.backend.identity.user.User;
import io.github.luminaire1337.propertyvista.backend.identity.verification.VerificationToken;
import io.github.luminaire1337.propertyvista.backend.notification.NotifiableEvent;

public record UserCreatedEvent(User user, VerificationToken token) implements NotifiableEvent {}
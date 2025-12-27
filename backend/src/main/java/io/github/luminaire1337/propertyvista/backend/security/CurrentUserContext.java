package io.github.luminaire1337.propertyvista.backend.security;

import io.github.luminaire1337.propertyvista.backend.entity.User;
import io.github.luminaire1337.propertyvista.backend.exception.ForbiddenAccessException;
import io.github.luminaire1337.propertyvista.backend.exception.UnauthorizedAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserContext {
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            throw new UnauthorizedAccessException("Nie jesteś zalogowany");
        }

        if (!(auth.getPrincipal() instanceof User user)) {
            throw new UnauthorizedAccessException("Nie jesteś zalogowany");
        }

        if (!user.isEnabled()) {
            throw new ForbiddenAccessException("Twoje konto nie zostało zweryfikowane. Sprawdź swoją skrzynkę e-mail, aby zweryfikować konto.");
        }

        if (!user.isAccountNonLocked()) {
            throw new ForbiddenAccessException("Twoje konto zostało zablokowane. Skontaktuj się z administratorem.");
        }

        return user;
    }

    public void ensureCurrentUserIsAdmin() {
        User user = getCurrentUser();
        if (!user.isAdmin()) {
            throw new ForbiddenAccessException("Nie masz uprawnień do wykonania tej akcji");
        }
    }
}

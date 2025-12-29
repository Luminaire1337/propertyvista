package io.github.luminaire1337.propertyvista.backend.security;

import io.github.luminaire1337.propertyvista.backend.entity.JwtUser;
import io.github.luminaire1337.propertyvista.backend.entity.User;
import io.github.luminaire1337.propertyvista.backend.exception.ForbiddenAccessException;
import io.github.luminaire1337.propertyvista.backend.exception.UnauthorizedAccessException;
import io.github.luminaire1337.propertyvista.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CurrentUserContext {
    private final UserService userService;
    
    public User getEntity() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            throw new UnauthorizedAccessException("Nie jesteś zalogowany");
        }

        if (!(auth.getPrincipal() instanceof JwtUser jwtUser)) {
            throw new UnauthorizedAccessException("Nie jesteś zalogowany");
        }

        User user = userService.getByUserId(jwtUser.id());

        if (!user.isVerified()) {
            throw new ForbiddenAccessException("Twoje konto nie zostało zweryfikowane. Sprawdź swoją skrzynkę e-mail, aby zweryfikować konto.");
        }

        if (user.isSuspended()) {
            throw new ForbiddenAccessException("Twoje konto zostało zablokowane. Skontaktuj się z administratorem.");
        }

        return user;
    }
}

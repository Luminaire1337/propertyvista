package io.github.luminaire1337.propertyvista.backend.security;

import io.github.luminaire1337.propertyvista.backend.entity.User;
import io.github.luminaire1337.propertyvista.backend.exception.ForbiddenAccessException;
import io.github.luminaire1337.propertyvista.backend.exception.UnauthorizedAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrentUserContext {
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            throw new UnauthorizedAccessException("User is not authenticated");
        }

        if (!(auth.getPrincipal() instanceof User user)) {
            throw new UnauthorizedAccessException("User is not authenticated");
        }

        if (!user.isEnabled()) {
            throw new ForbiddenAccessException("User is not verified");
        }

        if (!user.isAccountNonLocked()) {
            throw new ForbiddenAccessException("User account is suspended");
        }

        return user;
    }

    public UUID getCurrentUserId() {
        User user = getCurrentUser();
        return user.getId();
    }

    public void ensureCurrentUserIsAdmin() {
        User user = getCurrentUser();
        if (!user.isAdmin()) {
            throw new ForbiddenAccessException("User does not have admin privileges");
        }
    }
}

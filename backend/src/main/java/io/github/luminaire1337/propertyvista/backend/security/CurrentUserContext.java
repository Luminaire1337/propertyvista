package io.github.luminaire1337.propertyvista.backend.security;

import io.github.luminaire1337.propertyvista.backend.entity.User;
import io.github.luminaire1337.propertyvista.backend.entity.UserRole;
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

        if (auth.getPrincipal() instanceof User principal) {
            return principal;
        }

        throw new UnauthorizedAccessException("User is not authenticated");
    }

    public UUID getCurrentUserId() {
        User user = getCurrentUser();
        return user.getId();
    }

    public void ensureCurrentUserIsAdmin() {
        User user = getCurrentUser();
        if (user.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedAccessException("User does not have admin privileges");
        }
    }
}

package io.github.luminaire1337.propertyvista.backend.identity.user.repository;

import io.github.luminaire1337.propertyvista.backend.identity.user.entity.User;
import io.github.luminaire1337.propertyvista.backend.shared.EmailAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(EmailAddress email);

    boolean existsByEmail(EmailAddress email);
}

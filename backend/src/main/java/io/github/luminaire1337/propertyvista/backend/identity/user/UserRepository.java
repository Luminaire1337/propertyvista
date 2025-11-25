package io.github.luminaire1337.propertyvista.backend.identity.user;

import io.github.luminaire1337.propertyvista.backend.shared.EmailAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(EmailAddress email);

    boolean existsByEmail(EmailAddress email);
}

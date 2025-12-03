package io.github.luminaire1337.propertyvista.backend.repository;

import io.github.luminaire1337.propertyvista.backend.entity.User;
import io.github.luminaire1337.propertyvista.backend.vo.EmailAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(EmailAddress email);

    boolean existsByEmail(EmailAddress email);
}

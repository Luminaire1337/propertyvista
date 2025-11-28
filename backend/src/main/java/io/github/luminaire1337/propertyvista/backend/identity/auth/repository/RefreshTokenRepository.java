package io.github.luminaire1337.propertyvista.backend.identity.auth.repository;

import io.github.luminaire1337.propertyvista.backend.identity.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
}

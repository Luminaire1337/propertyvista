package io.github.luminaire1337.propertyvista.backend.identity.verification.repository;

import io.github.luminaire1337.propertyvista.backend.identity.verification.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
}

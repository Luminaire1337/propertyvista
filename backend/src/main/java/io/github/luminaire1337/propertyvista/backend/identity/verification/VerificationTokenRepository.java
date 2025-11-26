package io.github.luminaire1337.propertyvista.backend.identity.verification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
}

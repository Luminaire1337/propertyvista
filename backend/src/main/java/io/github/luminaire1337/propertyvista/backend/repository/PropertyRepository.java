package io.github.luminaire1337.propertyvista.backend.repository;

import io.github.luminaire1337.propertyvista.backend.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PropertyRepository extends JpaRepository<Property, UUID> {
    Optional<Property> findBySlug(String slug);

    boolean existsBySlug(String slug);
}

package io.github.luminaire1337.propertyvista.backend.entity;

import java.util.List;
import java.util.UUID;

public record JwtUser(
        UUID id,
        String email,
        List<String> roles
) {
}
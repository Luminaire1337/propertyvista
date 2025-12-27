package io.github.luminaire1337.propertyvista.backend.dto.response;

import io.github.luminaire1337.propertyvista.backend.entity.utility.PropertyStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record PropertyResponse(
        UUID id,
        String slug,
        String title,
        PropertyStatus status,
        String description,
        Double price,
        String city,
        Double area,
        Integer rooms,
        Boolean parking,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

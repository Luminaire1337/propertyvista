package io.github.luminaire1337.propertyvista.backend.dto.response;

import io.github.luminaire1337.propertyvista.backend.entity.utility.PropertyStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record PropertyListingResponse(
        UUID id,
        String slug,
        String title,
        PropertyStatus status,
        Double price,
        String city,
        Double area,
        Integer rooms,
        Boolean parking,
        LocalDateTime expiryDate,
        String primaryImagePath,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

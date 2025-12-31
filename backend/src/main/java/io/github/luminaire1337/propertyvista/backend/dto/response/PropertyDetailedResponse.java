package io.github.luminaire1337.propertyvista.backend.dto.response;

import io.github.luminaire1337.propertyvista.backend.entity.utility.PropertyStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PropertyDetailedResponse(
        UUID id,
        String slug,
        String title,
        UserPropertyDetailedResponse user,
        PropertyStatus status,
        String description,
        Double price,
        String city,
        Double area,
        Integer rooms,
        Boolean parking,
        List<String> imagePaths,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

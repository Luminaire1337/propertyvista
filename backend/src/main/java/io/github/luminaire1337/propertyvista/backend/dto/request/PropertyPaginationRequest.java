package io.github.luminaire1337.propertyvista.backend.dto.request;

import org.springframework.data.domain.Sort;

public record PropertyPaginationRequest(
        Integer page,
        Integer size,
        String sortField,
        Sort.Direction sortDirection,
        String city,
        Double minPrice,
        Double maxPrice,
        Integer minRooms,
        Integer maxRooms,
        Double minArea,
        Double maxArea,
        Boolean parking
) implements PaginationRequest {
}

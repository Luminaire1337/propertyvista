package io.github.luminaire1337.propertyvista.backend.dto.response;

import java.util.List;

public record PropertyPageResponse(
        List<PropertyListingResponse> content,
        Integer pageNumber,
        Integer pageSize,
        Long totalElements,
        Integer totalPages
) implements PageResponse<PropertyListingResponse> {
}

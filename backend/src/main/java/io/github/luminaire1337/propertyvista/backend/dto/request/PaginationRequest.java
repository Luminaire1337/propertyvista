package io.github.luminaire1337.propertyvista.backend.dto.request;

import org.springframework.data.domain.Sort;

public interface PaginationRequest {
    Integer page();

    Integer size();

    String sortField();

    Sort.Direction sortDirection();
}

package io.github.luminaire1337.propertyvista.backend.dto.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public interface PaginationRequest<T> {
    Integer page();

    Integer size();

    String sortField();

    Sort.Direction sortDirection();

    Specification<T> toSpecification();

    default Pageable toPageable() {
        int pageNumber = (page() != null && page() >= 0) ? page() : 0;
        int pageSize = (size() != null && size() > 0) ? size() : 20;
        String sortBy = (sortField() != null && !sortField().isBlank()) ? sortField() : "createdAt";
        Sort.Direction direction = (sortDirection() != null) ? sortDirection() : Sort.Direction.DESC;
        return PageRequest.of(pageNumber, pageSize, direction, sortBy);
    }
}

package io.github.luminaire1337.propertyvista.backend.dto.response;

import java.util.List;

public interface PageResponse<T> {
    List<T> content();

    Integer pageNumber();

    Integer pageSize();

    Long totalElements();

    Integer totalPages();
}

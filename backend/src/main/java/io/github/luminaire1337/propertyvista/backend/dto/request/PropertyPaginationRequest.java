package io.github.luminaire1337.propertyvista.backend.dto.request;

import io.github.luminaire1337.propertyvista.backend.entity.Property;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

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
) implements PaginationRequest<Property> {
    public Specification<Property> toSpecification() {
        return (root, cq, cb) -> {
            var predicates = cb.conjunction();

            if (city != null && !city.isBlank()) {
                predicates = cb.like(cb.lower(root.get("city")), "%" + city.toLowerCase() + "%");
            }
            if (minPrice != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            if (minRooms != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("rooms"), minRooms));
            }
            if (maxRooms != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("rooms"), maxRooms));
            }
            if (minArea != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("area"), minArea));
            }
            if (maxArea != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("area"), maxArea));
            }
            if (parking != null) {
                predicates = cb.and(predicates, cb.equal(root.get("parking"), parking));
            }

            return predicates;
        };
    }
}

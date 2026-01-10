package io.github.luminaire1337.propertyvista.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

public record CreatePaymentIntentRequest(
        @NotNull(message = "Ilość Property Points jest wymagana")
        @Range(min = 1, max = 100, message = "Można zakupić od 1 do 100 Property Points jednorazowo")
        Integer propertyPoints
) {
}

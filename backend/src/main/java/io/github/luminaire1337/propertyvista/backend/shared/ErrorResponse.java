package io.github.luminaire1337.propertyvista.backend.shared.internal.dto;

import java.util.HashMap;
import java.util.Map;

public record ErrorResponse(
        String message,
        Map<String, String> errors
) {
    public ErrorResponse(String message) {
        this(message, new HashMap<>());
    }
}

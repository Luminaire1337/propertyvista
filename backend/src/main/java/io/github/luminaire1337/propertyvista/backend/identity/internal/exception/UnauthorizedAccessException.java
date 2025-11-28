package io.github.luminaire1337.propertyvista.backend.identity.internal.exception;

import io.github.luminaire1337.propertyvista.backend.shared.RestApiException;
import org.springframework.http.HttpStatus;

public class UnauthorizedAccessException extends RestApiException {
    public UnauthorizedAccessException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}

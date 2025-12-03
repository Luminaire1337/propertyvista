package io.github.luminaire1337.propertyvista.backend.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedAccessException extends RestApiException {
    public UnauthorizedAccessException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}

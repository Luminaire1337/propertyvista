package io.github.luminaire1337.propertyvista.backend.exception;

import org.springframework.http.HttpStatus;

public class InvalidImageException extends RestApiException {
    public InvalidImageException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}

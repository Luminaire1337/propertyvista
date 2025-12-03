package io.github.luminaire1337.propertyvista.backend.exception;

import org.springframework.http.HttpStatus;

public class InvalidAccessTokenException extends RestApiException {
    public InvalidAccessTokenException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}

package io.github.luminaire1337.propertyvista.backend.exception;

import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends RestApiException {
    public InvalidRefreshTokenException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}

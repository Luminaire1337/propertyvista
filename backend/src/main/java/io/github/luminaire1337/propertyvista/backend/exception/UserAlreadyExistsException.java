package io.github.luminaire1337.propertyvista.backend.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends RestApiException {
    public UserAlreadyExistsException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
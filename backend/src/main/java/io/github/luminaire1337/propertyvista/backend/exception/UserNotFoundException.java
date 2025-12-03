package io.github.luminaire1337.propertyvista.backend.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends RestApiException {
    public UserNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
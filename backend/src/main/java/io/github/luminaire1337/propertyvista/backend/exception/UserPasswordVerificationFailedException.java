package io.github.luminaire1337.propertyvista.backend.exception;

import org.springframework.http.HttpStatus;

public class UserPasswordVerificationFailedException extends RestApiException {
    public UserPasswordVerificationFailedException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}

package io.github.luminaire1337.propertyvista.backend.exception;

import org.springframework.http.HttpStatus;

public class VerificationTokenVerificationFailedException extends RestApiException {
    public VerificationTokenVerificationFailedException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
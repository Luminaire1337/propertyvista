package io.github.luminaire1337.propertyvista.backend.identity.verification.exception;

import io.github.luminaire1337.propertyvista.backend.shared.RestApiException;
import org.springframework.http.HttpStatus;

public class VerificationTokenNotFoundException extends RestApiException {
    public VerificationTokenNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
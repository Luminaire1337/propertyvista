package io.github.luminaire1337.propertyvista.backend.identity.user.exception;

import io.github.luminaire1337.propertyvista.backend.shared.RestApiException;
import org.springframework.http.HttpStatus;

public class UserPasswordVerificationFailedException extends RestApiException {
    public UserPasswordVerificationFailedException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}

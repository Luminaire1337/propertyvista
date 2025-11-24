package io.github.luminaire1337.propertyvista.backend.shared;

import org.springframework.http.HttpStatus;

public abstract class RestApiException extends RuntimeException {
    private final HttpStatus status;

    public RestApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

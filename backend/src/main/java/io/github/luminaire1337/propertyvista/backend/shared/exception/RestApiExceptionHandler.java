package io.github.luminaire1337.propertyvista.backend.shared.exception;

import io.github.luminaire1337.propertyvista.backend.shared.RestApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class RestApiExceptionHandler {
    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<?> handleRestApiException(RestApiException ex) {
        return ResponseEntity
                .status(ex.getStatus() != null ? ex.getStatus() : HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }
}

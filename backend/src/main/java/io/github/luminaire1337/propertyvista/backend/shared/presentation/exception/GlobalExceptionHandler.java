package io.github.luminaire1337.propertyvista.backend.shared.presentation.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Order() // Fallback for every unhandled exception
public class GlobalExceptionHandler {
    private final boolean isDev;

    public GlobalExceptionHandler(@Value("${spring.profiles.active:}") String activeProfile) {
        this.isDev = "dev".equals(activeProfile);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", isDev ? ex.getMessage() : "An unexpected error occurred"));
    }
}

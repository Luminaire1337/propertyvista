package io.github.luminaire1337.propertyvista.backend.identity.auth.controller;

import io.github.luminaire1337.propertyvista.backend.identity.auth.dto.AuthResponse;
import io.github.luminaire1337.propertyvista.backend.identity.auth.dto.LoginRequest;
import io.github.luminaire1337.propertyvista.backend.identity.auth.dto.RefreshTokenRequest;
import io.github.luminaire1337.propertyvista.backend.identity.auth.dto.RegisterRequest;
import io.github.luminaire1337.propertyvista.backend.identity.auth.service.AuthService;
import io.github.luminaire1337.propertyvista.backend.identity.user.dto.UserResponse;
import io.github.luminaire1337.propertyvista.backend.shared.EmailAddress;
import io.github.luminaire1337.propertyvista.backend.shared.ErrorResponse;
import io.github.luminaire1337.propertyvista.backend.shared.PhoneNumber;
import io.github.luminaire1337.propertyvista.backend.shared.SafePassword;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/identity/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid request payload", content = {
                @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
        })
})
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user and obtain tokens",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User registered successfully", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class))
                    })
            }
    )
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        UserResponse userResponse = authService.register
                (
                        EmailAddress.valueOf(registerRequest.email()),
                        SafePassword.valueOf(registerRequest.password()),
                        registerRequest.firstName(),
                        registerRequest.lastName(),
                        PhoneNumber.valueOf(registerRequest.phoneNumber())
                );
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate a user and obtain tokens",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User authenticated successfully", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponse.class))
                    }),
                    @ApiResponse(responseCode = "403", description = "User account is inactive or banned", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))
                    })
            }
    )
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.login(
                EmailAddress.valueOf(loginRequest.email()),
                SafePassword.valueOf(loginRequest.password())
        );
        return ResponseEntity.status(HttpStatus.OK).body(authResponse);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Logout a user by invalidating their refresh token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User logged out successfully")
            }
    )
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        authService.logout(UUID.fromString(refreshTokenRequest.refreshToken()));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh authentication tokens using a valid refresh token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tokens refreshed successfully", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponse.class))
                    })
            }
    )
    public ResponseEntity<AuthResponse> refreshTokens(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        AuthResponse authResponse = authService.refreshAccessToken(
                UUID.fromString(refreshTokenRequest.refreshToken())
        );
        return ResponseEntity.status(HttpStatus.OK).body(authResponse);
    }
}

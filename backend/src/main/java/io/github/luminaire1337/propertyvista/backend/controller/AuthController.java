package io.github.luminaire1337.propertyvista.backend.controller;

import io.github.luminaire1337.propertyvista.backend.dto.request.LoginRequest;
import io.github.luminaire1337.propertyvista.backend.dto.request.RefreshTokenRequest;
import io.github.luminaire1337.propertyvista.backend.dto.response.AuthResponse;
import io.github.luminaire1337.propertyvista.backend.dto.response.ErrorResponse;
import io.github.luminaire1337.propertyvista.backend.service.AuthService;
import io.github.luminaire1337.propertyvista.backend.vo.EmailAddress;
import io.github.luminaire1337.propertyvista.backend.vo.SafePassword;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
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
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        AuthResponse authResponse = authService.login(
                EmailAddress.valueOf(loginRequest.email()),
                SafePassword.valueOf(loginRequest.password()),
                request.getHeader("User-Agent")
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
        authService.logout(refreshTokenRequest.refreshToken());
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
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest, HttpServletRequest request) {
        AuthResponse authResponse = authService.refreshAccessToken(
                refreshTokenRequest.refreshToken(),
                request.getHeader("User-Agent")
        );
        return ResponseEntity.status(HttpStatus.OK).body(authResponse);
    }
}

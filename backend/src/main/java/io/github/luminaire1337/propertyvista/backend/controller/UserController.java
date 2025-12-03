package io.github.luminaire1337.propertyvista.backend.controller;

import io.github.luminaire1337.propertyvista.backend.dto.request.RegisterRequest;
import io.github.luminaire1337.propertyvista.backend.dto.response.ErrorResponse;
import io.github.luminaire1337.propertyvista.backend.dto.response.UserResponse;
import io.github.luminaire1337.propertyvista.backend.entity.User;
import io.github.luminaire1337.propertyvista.backend.mapper.UserMapper;
import io.github.luminaire1337.propertyvista.backend.security.CurrentUserContext;
import io.github.luminaire1337.propertyvista.backend.service.UserService;
import io.github.luminaire1337.propertyvista.backend.vo.EmailAddress;
import io.github.luminaire1337.propertyvista.backend.vo.PhoneNumber;
import io.github.luminaire1337.propertyvista.backend.vo.SafePassword;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/identity/user")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for managing user information")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid request payload", content = {
                @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
        })
})
public class UserController {
    private final CurrentUserContext currentUserContext;
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping()
    @Operation(
            summary = "Get current user information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User information retrieved successfully", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class))
                    })
            }
    )
    public ResponseEntity<UserResponse> index() {
        User currentUser = currentUserContext.getCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDTO(currentUser));
    }


    @PostMapping()
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
        User user = userService.createUser
                (
                        EmailAddress.valueOf(registerRequest.email()),
                        SafePassword.valueOf(registerRequest.password()),
                        registerRequest.firstName(),
                        registerRequest.lastName(),
                        PhoneNumber.valueOf(registerRequest.phoneNumber()),
                        null
                );
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDTO(user));
    }

    @DeleteMapping()
    @Operation(
            summary = "Delete the current user's account",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User account deleted successfully", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class))
                    })
            }
    )
    public ResponseEntity<UserResponse> delete() {
        User user = currentUserContext.getCurrentUser();
        user = userService.deleteUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDTO(user));
    }
}

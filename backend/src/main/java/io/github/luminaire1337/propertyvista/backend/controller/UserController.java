package io.github.luminaire1337.propertyvista.backend.controller;

import io.github.luminaire1337.propertyvista.backend.dto.request.*;
import io.github.luminaire1337.propertyvista.backend.dto.response.ErrorResponse;
import io.github.luminaire1337.propertyvista.backend.dto.response.UserResponse;
import io.github.luminaire1337.propertyvista.backend.entity.User;
import io.github.luminaire1337.propertyvista.backend.mapper.UserMapper;
import io.github.luminaire1337.propertyvista.backend.security.CurrentUserContext;
import io.github.luminaire1337.propertyvista.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for managing user information")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid request payload", content = {
                @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
        }),
        @ApiResponse(responseCode = "401", description = "Unauthorized access", content = {
                @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
        }),
        @ApiResponse(responseCode = "403", description = "Forbidden access", content = {
                @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
        })
})
public class UserController {
    private final CurrentUserContext currentUserContext;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping()
    @Operation(
            summary = "Register a new user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User registered successfully", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class))
                    })
            }
    )
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        User user = userService.createUser
                (
                        registerRequest.email(),
                        registerRequest.password(),
                        registerRequest.firstName(),
                        registerRequest.lastName(),
                        registerRequest.phoneNumber(),
                        null
                );
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDTO(user));
    }

    @PostMapping("/verify-email")
    @Operation(
            summary = "Verify user's email address",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User email verified successfully")
            }
    )
    public ResponseEntity<Void> verifyUserEmail(@Valid @RequestBody TokenRequest tokenRequest) {
        userService.verifyUser(tokenRequest.token());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping({"/me", "/{id}"})
    @Operation(
            summary = "Get user information by ID or current user if no ID is provided",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User information retrieved successfully", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class))
                    })
            }
    )
    public ResponseEntity<UserResponse> getUser(@PathVariable(required = false) UUID id) {
        User user;

        // Check if an ID was provided and if the current user is an admin
        if (id != null) {
            currentUserContext.ensureCurrentUserIsAdmin();
            user = userService.getByUserId(id);
        } else {
            user = currentUserContext.getCurrentUser();
        }

        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDTO(user));
    }

    @DeleteMapping({"/me", "/{id}"})
    @Operation(
            summary = "Delete user account by ID or current user if no ID is provided",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User account deleted successfully", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class))
                    })
            }
    )
    public ResponseEntity<UserResponse> deleteUser(@PathVariable(required = false) UUID id) {
        User user;

        // Check if an ID was provided and if the current user is an admin
        if (id != null) {
            currentUserContext.ensureCurrentUserIsAdmin();
            user = userService.getByUserId(id);
        } else {
            user = currentUserContext.getCurrentUser();
        }

        user = userService.deleteUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDTO(user));
    }

    @PutMapping({"/me/email", "/{id}/email"})
    @Operation(
            summary = "Update user email by ID or current user if no ID is provided",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User email updated successfully", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class))
                    })
            }
    )
    public ResponseEntity<UserResponse> updateUserEmail(@PathVariable(required = false) UUID id, @Valid @RequestBody UpdateUserEmailRequest updateUserEmailRequest) {
        User user;

        // Check if an ID was provided and if the current user is an admin
        if (id != null) {
            currentUserContext.ensureCurrentUserIsAdmin();
            user = userService.getByUserId(id);
        } else {
            user = currentUserContext.getCurrentUser();
        }

        user = userService.updateUserEmail(user, updateUserEmailRequest.email());
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDTO(user));
    }

    @PutMapping({"/me/password", "/{id}/password"})
    @Operation(
            summary = "Update user password by ID or current user if no ID is provided",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User password updated successfully", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class))
                    })
            }
    )
    public ResponseEntity<UserResponse> updateUserPassword(@PathVariable(required = false) UUID id, @Valid @RequestBody UpdateUserPasswordRequest updateUserPasswordRequest) {
        User user;

        // Check if an ID was provided and if the current user is an admin
        if (id != null) {
            currentUserContext.ensureCurrentUserIsAdmin();
            user = userService.getByUserId(id);
        } else {
            user = currentUserContext.getCurrentUser();
        }

        user = userService.updateUserPassword(user, updateUserPasswordRequest.password());
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDTO(user));
    }

    @PutMapping("/{id}/role")
    @Operation(
            summary = "Update user role by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User role updated successfully", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class))
                    })
            }
    )
    public ResponseEntity<UserResponse> updateUserRole(@PathVariable UUID id, @Valid @RequestBody UpdateUserRoleRequest updateUserRoleRequest) {
        currentUserContext.ensureCurrentUserIsAdmin();
        User user = userService.getByUserId(id);
        user = userService.updateUserRole(user, updateUserRoleRequest.role());
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDTO(user));
    }

    @PutMapping("/{id}/status")
    @Operation(
            summary = "Update user status by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User status updated successfully", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class))
                    })
            }
    )
    public ResponseEntity<UserResponse> updateUserStatus(@PathVariable UUID id, @Valid @RequestBody UpdateUserStatusRequest updateUserStatusRequest) {
        currentUserContext.ensureCurrentUserIsAdmin();
        User user = userService.getByUserId(id);
        user = userService.updateUserStatus(user, updateUserStatusRequest.status());
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDTO(user));
    }
    
    @PutMapping({"/me/info", "/{id}/info"})
    @Operation(
            summary = "Update user information by ID or current user if no ID is provided",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User information updated successfully", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class))
                    })
            }
    )
    public ResponseEntity<UserResponse> updateUserInfo(@PathVariable(required = false) UUID id, @Valid @RequestBody UpdateUserInfoRequest updateUserInfoRequest) {
        User user;

        // Check if an ID was provided and if the current user is an admin
        if (id != null) {
            currentUserContext.ensureCurrentUserIsAdmin();
            user = userService.getByUserId(id);
        } else {
            user = currentUserContext.getCurrentUser();
        }

        user = userService.updateUserInfo(
                user,
                updateUserInfoRequest.firstName(),
                updateUserInfoRequest.lastName(),
                updateUserInfoRequest.phoneNumber()
        );
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDTO(user));
    }

    @PutMapping(path = {"/me/avatar", "/{id}/avatar"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Update user avatar image by ID or current user if no ID is provided",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User avatar image updated successfully", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class))
                    })
            }
    )
    public ResponseEntity<UserResponse> updateUserAvatarImage(@PathVariable(required = false) UUID id, @RequestParam("avatarImage") MultipartFile avatarImage) {
        User user;

        // Check if an ID was provided and if the current user is an admin
        if (id != null) {
            currentUserContext.ensureCurrentUserIsAdmin();
            user = userService.getByUserId(id);
        } else {
            user = currentUserContext.getCurrentUser();
        }

        user = userService.updateUserAvatarImage(user, avatarImage);
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDTO(user));
    }

    @DeleteMapping({"/me/avatar", "/{id}/avatar"})
    @Operation(
            summary = "Delete user avatar image by ID or current user if no ID is provided",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User avatar image deleted successfully", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class))
                    })
            }
    )
    public ResponseEntity<UserResponse> deleteUserAvatarImage(@PathVariable(required = false) UUID id) {
        User user;

        // Check if an ID was provided and if the current user is an admin
        if (id != null) {
            currentUserContext.ensureCurrentUserIsAdmin();
            user = userService.getByUserId(id);
        } else {
            user = currentUserContext.getCurrentUser();
        }

        user = userService.updateUserAvatarImage(user, null);
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDTO(user));
    }
}

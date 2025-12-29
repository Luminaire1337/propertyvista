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

    @GetMapping("/me")
    @Operation(
            summary = "Get current user information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User information retrieved successfully", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class))
                    })
            }
    )
    public ResponseEntity<UserResponse> getUser() {
        User user = currentUserContext.getEntity();
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDTO(user));
    }

    @DeleteMapping("/me")
    @Operation(
            summary = "Delete current user account",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User account deleted successfully", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class))
                    })
            }
    )
    public ResponseEntity<UserResponse> deleteUser() {
        User user = currentUserContext.getEntity();
        user = userService.deleteUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDTO(user));
    }

    @PutMapping("/me/email")
    @Operation(
            summary = "Update current user email",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User email updated successfully", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class))
                    })
            }
    )
    public ResponseEntity<UserResponse> updateUserEmail(@Valid @RequestBody UpdateUserEmailRequest updateUserEmailRequest) {
        User user = currentUserContext.getEntity();
        user = userService.updateUserEmail(user, updateUserEmailRequest.email());
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDTO(user));
    }

    @PutMapping("/me/password")
    @Operation(
            summary = "Update current user password",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User password updated successfully", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class))
                    })
            }
    )
    public ResponseEntity<UserResponse> updateUserPassword(@Valid @RequestBody UpdateUserPasswordRequest updateUserPasswordRequest) {
        User user = currentUserContext.getEntity();
        user = userService.updateUserPassword(user, updateUserPasswordRequest.password());
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDTO(user));
    }

    @PutMapping("/me/info")
    @Operation(
            summary = "Update current user information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User information updated successfully", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class))
                    })
            }
    )
    public ResponseEntity<UserResponse> updateUserInfo(@Valid @RequestBody UpdateUserInfoRequest updateUserInfoRequest) {
        User user = currentUserContext.getEntity();
        user = userService.updateUserInfo(
                user,
                updateUserInfoRequest.firstName(),
                updateUserInfoRequest.lastName(),
                updateUserInfoRequest.phoneNumber()
        );
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDTO(user));
    }

    @PutMapping(path = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Update current user avatar image",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User avatar image updated successfully", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class))
                    })
            }
    )
    public ResponseEntity<UserResponse> updateUserAvatarImage(@RequestParam("avatarImage") MultipartFile avatarImage) {
        User user = currentUserContext.getEntity();
        user = userService.updateUserAvatarImage(user, avatarImage);
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDTO(user));
    }

    @DeleteMapping("/me/avatar")
    @Operation(
            summary = "Delete current user avatar image",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User avatar image deleted successfully", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class))
                    })
            }
    )
    public ResponseEntity<UserResponse> deleteUserAvatarImage() {
        User user = currentUserContext.getEntity();
        user = userService.updateUserAvatarImage(user, null);
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDTO(user));
    }
}

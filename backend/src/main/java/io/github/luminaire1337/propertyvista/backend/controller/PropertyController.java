package io.github.luminaire1337.propertyvista.backend.controller;

import io.github.luminaire1337.propertyvista.backend.dto.request.CreatePropertyRequest;
import io.github.luminaire1337.propertyvista.backend.dto.request.PropertyPaginationRequest;
import io.github.luminaire1337.propertyvista.backend.dto.response.ErrorResponse;
import io.github.luminaire1337.propertyvista.backend.dto.response.PropertyDetailedResponse;
import io.github.luminaire1337.propertyvista.backend.dto.response.PropertyListingResponse;
import io.github.luminaire1337.propertyvista.backend.dto.response.PropertyResponse;
import io.github.luminaire1337.propertyvista.backend.entity.User;
import io.github.luminaire1337.propertyvista.backend.mapper.PropertyMapper;
import io.github.luminaire1337.propertyvista.backend.security.CurrentUserContext;
import io.github.luminaire1337.propertyvista.backend.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/properties")
@RequiredArgsConstructor
@Tag(name = "Property management", description = "Endpoints for managing properties")
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
public class PropertyController {
    private final PropertyService propertyService;
    private final PropertyMapper propertyMapper;
    private final CurrentUserContext currentUserContext;

    @GetMapping()
    @Operation(
            summary = "Get paginated list of properties",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of properties", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PropertyListingResponse.class))
                    })
            }
    )
    public ResponseEntity<List<PropertyListingResponse>> getProperties(@Valid @ModelAttribute() PropertyPaginationRequest paginationRequest) {
        List<PropertyListingResponse> properties = propertyService.getProperties(paginationRequest).stream()
                .map(propertyMapper::toListingDTO)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(properties);
    }

    @PostMapping()
    @Operation(
            summary = "Create a new property",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Property created successfully", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PropertyResponse.class))
                    })
            }
    )
    public ResponseEntity<PropertyResponse> createProperty(@Valid @RequestBody CreatePropertyRequest createPropertyRequest) {
        User user = currentUserContext.getEntity();
        PropertyResponse property = propertyMapper.toDTO(propertyService.createProperty(createPropertyRequest, user));
        return ResponseEntity.status(HttpStatus.CREATED).body(property);
    }

    @GetMapping("/{slug}")
    @Operation(
            summary = "Get property by slug",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of property", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PropertyDetailedResponse.class))
                    })
            }
    )
    public ResponseEntity<PropertyDetailedResponse> getPropertyBySlug(@PathVariable @NotBlank(message = "Identyfikator jest wymagany") String slug) {
        PropertyDetailedResponse property = propertyMapper.toDetailedDTO(propertyService.getPropertyBySlug(slug));
        return ResponseEntity.status(HttpStatus.OK).body(property);
    }
}

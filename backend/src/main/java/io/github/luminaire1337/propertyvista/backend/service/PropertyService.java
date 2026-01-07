package io.github.luminaire1337.propertyvista.backend.service;

import io.github.luminaire1337.propertyvista.backend.dto.email.PropertyApprovedEmail;
import io.github.luminaire1337.propertyvista.backend.dto.email.PropertyExpiredEmail;
import io.github.luminaire1337.propertyvista.backend.dto.email.PropertyRejectedEmail;
import io.github.luminaire1337.propertyvista.backend.entity.Property;
import io.github.luminaire1337.propertyvista.backend.entity.PropertyImage;
import io.github.luminaire1337.propertyvista.backend.entity.User;
import io.github.luminaire1337.propertyvista.backend.entity.utility.PropertyStatus;
import io.github.luminaire1337.propertyvista.backend.exception.BadRequestException;
import io.github.luminaire1337.propertyvista.backend.exception.NotFoundException;
import io.github.luminaire1337.propertyvista.backend.helper.BucketNames;
import io.github.luminaire1337.propertyvista.backend.repository.PropertyImageRepository;
import io.github.luminaire1337.propertyvista.backend.repository.PropertyRepository;
import io.minio.ObjectWriteResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final PropertyImageRepository propertyImageRepository;
    private final ImageService imageService;
    private final StorageService storageService;
    private final UserService userService;
    private final EmailService emailService;

    public Property getPropertyBySlug(String slug) {
        return propertyRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono nieruchomości o podanym identyfikatorze"));
    }

    public Property getPublishedPropertyBySlug(String slug) {
        Property property = getPropertyBySlug(slug);
        if (!property.isPublished()) {
            throw new NotFoundException("Nie znaleziono nieruchomości o podanym identyfikatorze");
        }
        return property;
    }

    public List<Property> findAllExpiredProperties() {
        LocalDateTime now = LocalDateTime.now();
        return propertyRepository.findAll().stream()
                .filter(Property::isPublished)
                .filter(property -> property.getExpiryDate().isBefore(now))
                .toList();
    }

    @Transactional
    public void updatePropertiesInBatch(List<Property> properties) {
        propertyRepository.saveAll(properties);
    }

    public void notifyExpiredPropertiesOwners(List<Property> expiredProperties) {
        for (var property : expiredProperties) {
            emailService.sendEmailAsync(new PropertyExpiredEmail(property, property.getUser()));
        }
    }

    public Page<Property> getPaginatedProperties(Specification<Property> spec, Pageable pageable) {
        return propertyRepository.findAll(
                // Only published properties
                Specification.where(spec).and((root, cq, cb) ->
                        cb.equal(root.get("status"), PropertyStatus.PUBLISHED)
                ),
                pageable
        );
    }

    public Page<Property> getUserPaginatedProperties(User user, Specification<Property> spec, Pageable pageable) {
        return propertyRepository.findAll(
                Specification.where(spec).and((root, cq, cb) ->
                        cb.equal(root.get("user"), user)
                ),
                pageable
        );
    }

    public Property createProperty(
            String title,
            String description,
            Double price,
            String city,
            Double area,
            Integer rooms,
            Boolean parking,
            List<MultipartFile> images,
            String primaryImagePath,
            Integer daysValid,
            User user
    ) {

        // Check if user has enough property points
        if (user.getPropertyPoints() < daysValid) {
            throw new BadRequestException("Niewystarczająca liczba Property Points do utworzenia nowej oferty");
        }

        // Check all images
        for (var image : images) {
            if (!imageService.isImageValid(image)) {
                throw new BadRequestException("Jedno lub więcej zdjęć jest nieprawidłowych");
            }
        }

        // Take user's property points
        var success = userService.takeUserPropertyPoints(user, daysValid);
        if (!success) {
            throw new BadRequestException("Nie udało się pobrać Property Points z twojego konta");
        }

        // Calculate expiry date
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(daysValid);

        Property property = Property.builder()
                .title(title)
                .description(description)
                .price(price)
                .city(city)
                .area(area)
                .rooms(rooms)
                .parking(parking)
                .expiryDate(expiryDate)
                .user(user)
                .build();

        property = propertyRepository.save(property);

        // Now upload images
        boolean isPrimarySet = false;
        List<ObjectWriteResponse> uploadedImages = new ArrayList<>();
        for (var image : images) {
            String newImageName = imageService.generateImageFileName(image);
            Boolean isPrimary = Objects.equals(image.getOriginalFilename(), primaryImagePath);
            var uploadResponse = storageService.uploadFile(
                    BucketNames.PRIVATE_PROPERTY_IMAGES,
                    newImageName,
                    image
            );

            if (uploadResponse != null) {
                PropertyImage propertyImage = PropertyImage.builder()
                        .property(property)
                        .imagePath(newImageName)
                        .primary(isPrimary)
                        .build();
                propertyImageRepository.save(propertyImage);
                if (isPrimary)
                    isPrimarySet = true;

                uploadedImages.add(uploadResponse);
            }
        }

        if (uploadedImages.size() != images.size())
            log.warn("Not all images were uploaded successfully for property ID {}", property.getId());

        if (!isPrimarySet && !uploadedImages.isEmpty()) {
            log.warn("No primary image was set for property ID {}. Setting the first uploaded image as primary.", property.getId());
            PropertyImage firstImage = propertyImageRepository.findFirstByProperty(property).orElse(null);
            if (firstImage != null) {
                firstImage.setPrimary(true);
                propertyImageRepository.save(firstImage);
            }
        }

        // Validate images content asynchronously
        UUID propertyId = property.getId();
        imageService.validateImagesContentAsync(uploadedImages, (allValid) -> {
                    Property finalProperty = propertyRepository.findById(propertyId).orElse(null);
                    if (finalProperty != null) {
                        if (allValid) {
                            storageService.moveFilesBetweenBucketsInBatch(
                                    BucketNames.PRIVATE_PROPERTY_IMAGES,
                                    BucketNames.PUBLIC_PROPERTY_IMAGES,
                                    uploadedImages.stream().map(ObjectWriteResponse::object).toList()
                            );
                            finalProperty.setStatus(PropertyStatus.PUBLISHED);
                            propertyRepository.save(finalProperty);
                            log.info("Property {} is now available to the public", propertyId);

                            emailService.sendEmailAsync(new PropertyApprovedEmail(finalProperty, finalProperty.getUser()));
                        } else {
                            // Refund property points to user
                            User finalUser = finalProperty.getUser();
                            userService.giveUserPropertyPoints(finalUser, daysValid);

                            finalProperty.setStatus(PropertyStatus.HIDDEN);
                            propertyRepository.save(finalProperty);
                            log.error("Image validation failed for property {}", propertyId);

                            emailService.sendEmailAsync(new PropertyRejectedEmail(finalProperty, finalProperty.getUser()));
                        }
                    }
                }
        );

        return property;
    }

    @Transactional
    public Property updateProperty(
            String slug,
            String title,
            String description,
            Double price,
            String city,
            Double area,
            Integer rooms,
            Boolean parking,
            List<MultipartFile> images,
            String primaryImagePath,
            Integer daysValid,
            User user
    ) {
        Property property = getPropertyBySlug(slug);

        // Check if user owns the property
        if (!property.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Nie masz uprawnień do edycji tej nieruchomości");
        }

        // Update only fields that are not null
        if (title != null && !title.isBlank()) {
            property.setTitle(title);
        }
        if (description != null) {
            property.setDescription(description);
        }
        if (price != null) {
            property.setPrice(price);
        }
        if (city != null && !city.isBlank()) {
            property.setCity(city);
        }
        if (area != null) {
            property.setArea(area);
        }
        if (rooms != null) {
            property.setRooms(rooms);
        }
        if (parking != null) {
            property.setParking(parking);
        }

        // Handle daysValid extension
        if (daysValid != null) {
            // Check if user has enough property points
            if (user.getPropertyPoints() < daysValid) {
                throw new BadRequestException("Niewystarczająca liczba Property Points do przedłużenia ogłoszenia");
            }

            // Take user's property points
            var success = userService.takeUserPropertyPoints(user, daysValid);
            if (!success) {
                throw new BadRequestException("Nie udało się pobrać Property Points z twojego konta");
            }

            // Extend expiry date
            LocalDateTime newExpiryDate = property.getExpiryDate().plusDays(daysValid);
            property.setExpiryDate(newExpiryDate);
        }

        // Handle images update
        if (images != null && !images.isEmpty()) {
            // Check all images
            for (var image : images) {
                if (!imageService.isImageValid(image)) {
                    throw new BadRequestException("Jedno lub więcej zdjęć jest nieprawidłowych");
                }
            }

            // Remove existing images in batch
            propertyImageRepository.deleteAll(property.getImages());

            // Upload new images
            boolean isPrimarySet = false;
            List<ObjectWriteResponse> uploadedImages = new ArrayList<>();

            for (var image : images) {
                String newImageName = imageService.generateImageFileName(image);
                Boolean isPrimary = primaryImagePath != null && Objects.equals(image.getOriginalFilename(), primaryImagePath);

                var uploadResponse = storageService.uploadFile(
                        BucketNames.PRIVATE_PROPERTY_IMAGES,
                        newImageName,
                        image
                );

                if (uploadResponse != null) {
                    PropertyImage propertyImage = PropertyImage.builder()
                            .property(property)
                            .imagePath(newImageName)
                            .primary(isPrimary)
                            .build();
                    propertyImageRepository.save(propertyImage);

                    if (isPrimary)
                        isPrimarySet = true;

                    uploadedImages.add(uploadResponse);
                }
            }

            if (uploadedImages.size() != images.size())
                log.warn("Not all images were uploaded successfully for property ID {}", property.getId());

            // If no primary was set in new images, keep existing primary or set first new image
            if (!isPrimarySet && !uploadedImages.isEmpty()) {
                log.info("No primary image specified in new images for property ID {}", property.getId());
                PropertyImage firstImage = propertyImageRepository.findFirstByProperty(property).orElse(null);
                if (firstImage != null) {
                    firstImage.setPrimary(true);
                    propertyImageRepository.save(firstImage);
                }
            }


            // Set property status back to UNVERIFIED after update
            property.setStatus(PropertyStatus.UNVERIFIED);

            // Validate new images content asynchronously
            UUID propertyId = property.getId();
            imageService.validateImagesContentAsync(uploadedImages, (allValid) -> {
                Property finalProperty = propertyRepository.findById(propertyId).orElse(null);
                if (finalProperty != null) {
                    if (allValid) {
                        storageService.moveFilesBetweenBucketsInBatch(
                                BucketNames.PRIVATE_PROPERTY_IMAGES,
                                BucketNames.PUBLIC_PROPERTY_IMAGES,
                                uploadedImages.stream().map(ObjectWriteResponse::object).toList()
                        );
                        finalProperty.setStatus(PropertyStatus.PUBLISHED);
                        propertyRepository.save(finalProperty);
                        log.info("New images validated and moved to public bucket for property {}", propertyId);
                    } else {
                        finalProperty.setStatus(PropertyStatus.HIDDEN);
                        propertyRepository.save(finalProperty);
                        log.error("Image validation failed for new images in property {}", propertyId);
                    }
                }
            });
        } else if (primaryImagePath != null && !primaryImagePath.isBlank()) {
            // Update primary image if only primaryImagePath is specified (without new images)
            // First, remove primary flag from all images
            property.getImages().forEach(img -> {
                if (img.isPrimary()) {
                    img.setPrimary(false);
                    propertyImageRepository.save(img);
                }
            });

            // Set new primary image
            property.getImages().stream()
                    .filter(img -> img.getImagePath().equals(primaryImagePath))
                    .findFirst()
                    .ifPresentOrElse(
                            img -> {
                                img.setPrimary(true);
                                propertyImageRepository.save(img);
                            },
                            () -> {
                                throw new BadRequestException("Nie znaleziono zdjęcia o podanej ścieżce");
                            }
                    );
        }

        return propertyRepository.save(property);
    }
}

package io.github.luminaire1337.propertyvista.backend.service;

import io.github.luminaire1337.propertyvista.backend.dto.email.PropertyApprovedEmail;
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

    public Page<Property> getPaginatedProperties(Specification<Property> spec, Pageable pageable) {
        return propertyRepository.findAll(spec, pageable);
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
}

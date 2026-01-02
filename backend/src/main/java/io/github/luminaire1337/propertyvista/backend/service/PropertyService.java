package io.github.luminaire1337.propertyvista.backend.service;

import io.github.luminaire1337.propertyvista.backend.dto.request.CreatePropertyRequest;
import io.github.luminaire1337.propertyvista.backend.entity.Property;
import io.github.luminaire1337.propertyvista.backend.entity.PropertyImage;
import io.github.luminaire1337.propertyvista.backend.entity.User;
import io.github.luminaire1337.propertyvista.backend.exception.BadRequestException;
import io.github.luminaire1337.propertyvista.backend.exception.NotFoundException;
import io.github.luminaire1337.propertyvista.backend.helper.BucketNames;
import io.github.luminaire1337.propertyvista.backend.mapper.PropertyMapper;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final PropertyImageRepository propertyImageRepository;
    private final ImageService imageService;
    private final StorageService storageService;
    private final PropertyMapper propertyMapper;

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
    public void bulkUpdateProperties(List<Property> properties) {
        propertyRepository.saveAll(properties);
    }

    public Page<Property> getPaginatedProperties(Specification<Property> spec, Pageable pageable) {
        return propertyRepository.findAll(spec, pageable);
    }

    public Property createProperty(CreatePropertyRequest request, User owner) {
        // Check all images first
        for (var image : request.images()) {
            if (!imageService.isImageValid(image)) {
                throw new BadRequestException("Jedno lub więcej zdjęć jest nieprawidłowych");
            }
        }

        Property property = Property.builder()
                .title(request.title())
                .description(request.description())
                .price(request.price())
                .city(request.city())
                .area(request.area())
                .rooms(request.rooms())
                .parking(request.parking())
                .user(owner)
                .build();

        property = propertyRepository.save(property);

        // Now upload images
        boolean isPrimarySet = false;
        List<ObjectWriteResponse> uploadedImages = new ArrayList<>();
        for (var image : request.images()) {
            String newImageName = imageService.generateImageFileName(image);
            Boolean isPrimary = image.getName().equals(request.primaryImagePath());
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

        if (uploadedImages.size() != request.images().length)
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
                    // TODO: publish the property if all images are valid, send an email to the user etc.
                }
        );

        // TODO: take Property Points out of user's account
        return property;
    }
}

package io.github.luminaire1337.propertyvista.backend.entity.listener;

import io.github.luminaire1337.propertyvista.backend.entity.PropertyImage;
import io.github.luminaire1337.propertyvista.backend.helper.BucketNames;
import io.github.luminaire1337.propertyvista.backend.service.StorageService;
import jakarta.persistence.PreRemove;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PropertyImageListener {
    private final StorageService storageService;

    @PreRemove()
    public void preRemove(PropertyImage propertyImage) {
        storageService.deleteFileIfExists(propertyImage.getProperty().isPublished()
                        ? BucketNames.PUBLIC_PROPERTY_IMAGES
                        : BucketNames.PRIVATE_PROPERTY_IMAGES,
                propertyImage.getImagePath()
        );
    }
}

package io.github.luminaire1337.propertyvista.backend.mapper;

import io.github.luminaire1337.propertyvista.backend.dto.response.PropertyDetailedResponse;
import io.github.luminaire1337.propertyvista.backend.dto.response.PropertyListingResponse;
import io.github.luminaire1337.propertyvista.backend.dto.response.PropertyResponse;
import io.github.luminaire1337.propertyvista.backend.entity.Property;
import io.github.luminaire1337.propertyvista.backend.entity.PropertyImage;
import io.github.luminaire1337.propertyvista.backend.helper.BucketNames;
import io.github.luminaire1337.propertyvista.backend.service.StorageService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PropertyMapper {
    @Autowired
    protected StorageService storageService;

    public abstract PropertyResponse toDTO(Property property);

    @Mapping(target = "primaryImagePath", expression = "java(property.isPublished() ? mapImage(property.getPrimaryImage()) : null)")
    public abstract PropertyListingResponse toListingDTO(Property property);

    @Mapping(target = "imagePaths", expression = "java(property.isPublished() ? property.getImages().stream().map(this::mapImage).toList() : java.util.Collections.emptyList())")
    public abstract PropertyDetailedResponse toDetailedDTO(Property property);

    @Named("mapImage")
    protected String mapImage(PropertyImage propertyImage) {
        String fileName = propertyImage != null ? propertyImage.getImagePath() : null;
        return fileName != null
                ? storageService.getPublicFileUrl(BucketNames.PUBLIC_PROPERTY_IMAGES, fileName)
                : null;
    }
}

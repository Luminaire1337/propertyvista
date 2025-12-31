package io.github.luminaire1337.propertyvista.backend.mapper;

import io.github.luminaire1337.propertyvista.backend.dto.response.UserPropertyDetailedResponse;
import io.github.luminaire1337.propertyvista.backend.dto.response.UserResponse;
import io.github.luminaire1337.propertyvista.backend.entity.User;
import io.github.luminaire1337.propertyvista.backend.helper.BucketNames;
import io.github.luminaire1337.propertyvista.backend.service.StorageService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    @Autowired
    protected StorageService storageService;

    @Mapping(target = "avatarImagePath", expression = "java(mapAvatar(user.getAvatarImagePath()))")
    public abstract UserResponse toDTO(User user);

    @Mapping(target = "avatarImagePath", expression = "java(mapAvatar(user.getAvatarImagePath()))")
    public abstract UserPropertyDetailedResponse toPropertyDetailedDTO(User user);

    @Named("mapAvatar")
    protected String mapAvatar(String fileName) {
        return fileName != null
                ? storageService.getPublicFileUrl(BucketNames.PUBLIC_AVATAR_IMAGES, fileName)
                : null;
    }
}

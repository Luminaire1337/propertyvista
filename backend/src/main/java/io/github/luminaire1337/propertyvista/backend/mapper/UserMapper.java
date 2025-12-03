package io.github.luminaire1337.propertyvista.backend.mapper;

import io.github.luminaire1337.propertyvista.backend.dto.response.UserResponse;
import io.github.luminaire1337.propertyvista.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "email", expression = "java(String.valueOf(user.getEmail()))")
    @Mapping(target = "phoneNumber", expression = "java(String.valueOf(user.getPhoneNumber()))")
    @Mapping(target = "avatarImagePath", expression = "java(user.getAvatarImagePath() != null ? String.valueOf(user.getAvatarImagePath()) : null)")
    UserResponse toDTO(User user);
}

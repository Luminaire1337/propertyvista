package io.github.luminaire1337.propertyvista.backend.identity.user;

import io.github.luminaire1337.propertyvista.backend.identity.user.dto.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toDTO(User user);
}

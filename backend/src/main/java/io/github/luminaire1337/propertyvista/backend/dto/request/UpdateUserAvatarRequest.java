package io.github.luminaire1337.propertyvista.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record UpdateUserAvatarRequest(
        @NotNull(message = "Awatar jest wymagany")
        MultipartFile avatarImage
) {
}

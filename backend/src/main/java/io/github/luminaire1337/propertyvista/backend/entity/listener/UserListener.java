package io.github.luminaire1337.propertyvista.backend.entity.listener;

import io.github.luminaire1337.propertyvista.backend.entity.User;
import io.github.luminaire1337.propertyvista.backend.helper.BucketNames;
import io.github.luminaire1337.propertyvista.backend.service.StorageService;
import jakarta.persistence.PreRemove;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserListener {
    private final StorageService storageService;

    @PreRemove()
    public void preRemove(User user) {
        var avatarImage = user.getAvatarImagePath();
        if (avatarImage == null) {
            return;
        }

        storageService.deleteFile(
                BucketNames.PUBLIC_AVATAR_IMAGES,
                avatarImage
        );
    }
}

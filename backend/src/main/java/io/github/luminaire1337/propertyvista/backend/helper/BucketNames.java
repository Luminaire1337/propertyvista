package io.github.luminaire1337.propertyvista.backend.helper;

import java.util.List;

public abstract class BucketNames {
    public static final String PUBLIC_AVATAR_IMAGES = "public-avatar-images";
    public static final String PRIVATE_AVATAR_IMAGES = "private-avatar-images";

    public static List<String> getAllBucketNames() {
        return List.of(PUBLIC_AVATAR_IMAGES, PRIVATE_AVATAR_IMAGES);
    }
}

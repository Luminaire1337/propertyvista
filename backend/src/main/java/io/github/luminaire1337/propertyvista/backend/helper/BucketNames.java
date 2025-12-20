package io.github.luminaire1337.propertyvista.backend.helper;

import java.util.Map;

public abstract class BucketNames {
    public static final String PUBLIC_AVATAR_IMAGES = "public-avatar-images";
    public static final String PRIVATE_AVATAR_IMAGES = "private-avatar-images";
    public static final String PUBLIC_PROPERTY_IMAGES = "public-property-images";
    public static final String PRIVATE_PROPERTY_IMAGES = "private-property-images";

    public static Map<String, Boolean> getAllBuckets() {
        return Map.of(
                PUBLIC_AVATAR_IMAGES, true,
                PRIVATE_AVATAR_IMAGES, false,
                PUBLIC_PROPERTY_IMAGES, true,
                PRIVATE_PROPERTY_IMAGES, false
        );
    }
}

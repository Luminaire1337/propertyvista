package io.github.luminaire1337.propertyvista.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final MinioService minioService;

    private final long maxFileSize = 5 * 1024 * 1024; // 5 MB
    private final String[] allowedImageTypes = {
            "image/jpeg",
            "image/png",
    };
    private final Map<String, String> imageTypeExtensions = new HashMap<>(
            Map.of(
                    "image/jpeg", "jpg",
                    "image/png", "png"
            )
    );

    public boolean isImageValid(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return false;
        }

        if (image.getSize() > maxFileSize) {
            return false;
        }

        String contentType = image.getContentType();
        if (contentType == null) {
            return false;
        }

        for (String allowedType : allowedImageTypes) {
            if (allowedType.equalsIgnoreCase(contentType)) {
                return true;
            }
        }

        return false;
    }

    public String getImageExtension(MultipartFile image) {
        String contentType = image.getContentType();
        return imageTypeExtensions.get(contentType);
    }

    @Async
    public void processImage(MultipartFile image, Consumer<Boolean> callback) {
        // TODO: implement image verification
        callback.accept(true);
    }
}

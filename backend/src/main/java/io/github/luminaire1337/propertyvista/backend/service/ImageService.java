package io.github.luminaire1337.propertyvista.backend.service;

import com.google.api.core.ApiFuture;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import io.minio.ObjectWriteResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageService {
    private final StorageService storageService;

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

    @Value("${PROPERTYVISTA_GOOGLE_CLOUD_KEY}")
    private String googleCloudApiKey;

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

    public String generateImageFileName(MultipartFile image) {
        return UUID.randomUUID() + "." + getImageExtension(image);
    }

    @Async
    public void validateImagesContentAsync(List<ObjectWriteResponse> images, Consumer<Boolean> callback) {
        // Check if Google API key is set
        if (googleCloudApiKey == null || googleCloudApiKey.isBlank()) {
            log.warn("Google Cloud API key is not set. Skipping image content validation.");
            callback.accept(true);
            return;
        }

        HeaderProvider headerProvider = () -> Map.of("X-Goog-Api-Key", googleCloudApiKey);

        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create(
                ImageAnnotatorSettings.newBuilder()
                        .setHeaderProvider(headerProvider)
                        .setCredentialsProvider(NoCredentialsProvider.create())
                        .build()
        )) {
            List<AnnotateImageRequest> requests = new ArrayList<>();

            for (ObjectWriteResponse imageResponse : images) {
                String bucketName = imageResponse.bucket();
                String objectName = imageResponse.object();
                byte[] imageContent = storageService.getFileContent(bucketName, objectName);

                var img = Image.newBuilder()
                        .setContent(ByteString.copyFrom(imageContent))
                        .build();
                var feature = Feature.newBuilder()
                        .setType(Feature.Type.SAFE_SEARCH_DETECTION)
                        .build();
                var request = AnnotateImageRequest.newBuilder()
                        .addFeatures(feature)
                        .setImage(img)
                        .build();
                requests.add(request);
            }

            ApiFuture<BatchAnnotateImagesResponse> future = vision.batchAnnotateImagesCallable()
                    .futureCall(BatchAnnotateImagesRequest.newBuilder()
                            .addAllRequests(requests)
                            .build()
                    );

            future.addListener(() -> {
                try {
                    BatchAnnotateImagesResponse response = future.get();
                    for (AnnotateImageResponse imgResponse : response.getResponsesList()) {
                        if (imgResponse.hasError()) {
                            log.error("Image content validation error: {}", imgResponse.getError().getMessage());
                            callback.accept(false);
                            return;
                        }

                        SafeSearchAnnotation annotation = imgResponse.getSafeSearchAnnotation();
                        if (annotation.getAdultValue() >= Likelihood.LIKELY_VALUE ||
                                annotation.getViolenceValue() >= Likelihood.LIKELY_VALUE ||
                                annotation.getRacyValue() >= Likelihood.LIKELY_VALUE) {
                            log.warn("Inappropriate content detected in image: Adult={}, Violence={}, Racy={}",
                                    annotation.getAdult(), annotation.getViolence(), annotation.getRacy());
                            callback.accept(false);
                            return;
                        }
                    }
                    callback.accept(true);
                } catch (Exception e) {
                    log.error("Error processing image validation response: {}", e.getMessage());
                    callback.accept(false);
                }
            }, Runnable::run);
        } catch (Exception e) {
            log.error("Error during image validation: {}", e.getMessage());
            callback.accept(false);
        }
    }
}

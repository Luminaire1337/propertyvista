package io.github.luminaire1337.propertyvista.backend.service;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;

    public void deleteObjectIfExists(String bucketName, String objectName) {
        try {
            boolean found = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            ) != null;

            if (found) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .build()
                );
                log.info("Deleted object '{}' from bucket '{}'", objectName, bucketName);
            } else {
                log.info("Object '{}' not found in bucket '{}', no deletion performed", objectName, bucketName);
            }
        } catch (Exception e) {
            log.error("Error while checking or deleting object '{}' from bucket '{}': {}", objectName, bucketName, e.getMessage());
        }
    }

    public ObjectWriteResponse uploadFile(String bucketName, String objectName, MultipartFile file) {
        try {
            var response = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            log.info("Uploaded image '{}' to bucket '{}'", objectName, bucketName);
            return response;
        } catch (Exception e) {
            log.error("Error while uploading image '{}' to bucket '{}': {}", objectName, bucketName, e.getMessage());
            return null;
        }
    }

    public void moveObjectBetweenBuckets(String sourceBucket, String destinationBucket, String objectName) {
        try {
            // Copy the object to the new bucket
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .source(CopySource.builder()
                                    .bucket(sourceBucket)
                                    .object(objectName)
                                    .build())
                            .bucket(destinationBucket)
                            .object(objectName)
                            .build()
            );

            // Delete the object from the source bucket
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(sourceBucket)
                            .object(objectName)
                            .build()
            );

            log.info("Moved object '{}' from bucket '{}' to bucket '{}'", objectName, sourceBucket, destinationBucket);
        } catch (Exception e) {
            log.error("Error while moving object '{}' from bucket '{}' to bucket '{}': {}", objectName, sourceBucket, destinationBucket, e.getMessage());
        }
    }

    public String generatePresignedUrl(String bucketName, String objectName) {
        try {
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(60 * 60) // URL valid for 1 hour
                            .build()
            );
            log.info("Generated presigned URL for object '{}' in bucket '{}'", objectName, bucketName);
            return url;
        } catch (Exception e) {
            log.error("Error while generating presigned URL for object '{}' in bucket '{}': {}", objectName, bucketName, e.getMessage());
            return null;
        }
    }
}

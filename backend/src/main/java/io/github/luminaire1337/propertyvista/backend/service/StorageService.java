package io.github.luminaire1337.propertyvista.backend.service;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {
    private final MinioClient minioClient;

    @Value("${propertyvista.storage.public-url}")
    private String publicStorageUrl;

    public void deleteFileIfExists(String bucketName, String fileName) {
        try {
            boolean found = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            ) != null;

            if (found) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)
                                .build()
                );
                log.info("Deleted object '{}' from bucket '{}'", fileName, bucketName);
            } else {
                log.info("Object '{}' not found in bucket '{}', no deletion performed", fileName, bucketName);
            }
        } catch (Exception e) {
            log.error("Error while checking or deleting object '{}' from bucket '{}': {}", fileName, bucketName, e.getMessage());
        }
    }

    public ObjectWriteResponse uploadFile(String bucketName, String fileName, MultipartFile file) {
        try {
            var response = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            log.info("Uploaded image '{}' to bucket '{}'", fileName, bucketName);
            return response;
        } catch (Exception e) {
            log.error("Error while uploading image '{}' to bucket '{}': {}", fileName, bucketName, e.getMessage());
            return null;
        }
    }

    public void moveFileBetweenBuckets(String sourceBucket, String destinationBucket, String fileName) {
        try {
            // Copy the object to the new bucket
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .source(CopySource.builder()
                                    .bucket(sourceBucket)
                                    .object(fileName)
                                    .build())
                            .bucket(destinationBucket)
                            .object(fileName)
                            .build()
            );

            // Delete the object from the source bucket
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(sourceBucket)
                            .object(fileName)
                            .build()
            );

            log.info("Moved object '{}' from bucket '{}' to bucket '{}'", fileName, sourceBucket, destinationBucket);
        } catch (Exception e) {
            log.error("Error while moving object '{}' from bucket '{}' to bucket '{}': {}", fileName, sourceBucket, destinationBucket, e.getMessage());
        }
    }

    public String getPublicFileUrl(String bucketName, String fileName) {
        return "%s/%s/%s".formatted(publicStorageUrl, bucketName, fileName);
    }

    public byte[] getFileContent(String bucketName, String fileName) {
        try {
            var stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
            return stream.readAllBytes();
        } catch (Exception e) {
            log.error("Error while retrieving content of object '{}' from bucket '{}': {}", fileName, bucketName, e.getMessage());
            return null;
        }
    }
}

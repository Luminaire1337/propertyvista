package io.github.luminaire1337.propertyvista.backend.service;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {
    private final MinioClient minioClient;

    @Value("${PROPERTYVISTA_STORAGE_URL}")
    private String storageUrl;

    @Value("${PROPERTYVISTA_PUBLIC_STORAGE_URL}")
    private String publicStorageUrl;

    public void deleteFile(String bucketName, String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
            log.info("Deleted object '{}' from bucket '{}'", fileName, bucketName);
        } catch (Exception e) {
            log.error("Error while checking or deleting object '{}' from bucket '{}': {}", fileName, bucketName, e.getMessage());
        }
    }

    public void deleteFilesInBatch(String bucketName, List<String> fileNames) {
        for (String fileName : fileNames) {
            deleteFile(bucketName, fileName);
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

    public void moveFilesBetweenBucketsInBatch(String sourceBucket, String destinationBucket, List<String> fileNames) {
        for (String fileName : fileNames) {
            moveFileBetweenBuckets(sourceBucket, destinationBucket, fileName);
        }
    }

    public String getPublicFileUrl(String bucketName, String fileName) {
        return "%s/%s/%s".formatted(
                publicStorageUrl != null && !publicStorageUrl.isBlank() ? publicStorageUrl : storageUrl,
                bucketName,
                fileName
        );
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

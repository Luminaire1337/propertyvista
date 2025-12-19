package io.github.luminaire1337.propertyvista.backend.runners;

import io.github.luminaire1337.propertyvista.backend.helper.BucketNames;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(1)
public class MinioInitializer implements ApplicationRunner {
    private final MinioClient minioClient;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var requiredBuckets = BucketNames.getAllBucketNames();
        for (String bucketName : requiredBuckets) {
            try {
                boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
                if (!found) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                    log.info("Created missing MinIO bucket: {}", bucketName);
                }
            } catch (MinioException e) {
                log.error("Error while checking/creating MinIO bucket: {}", bucketName, e);
            }
        }
    }
}

package io.github.luminaire1337.propertyvista.backend.runners;

import io.github.luminaire1337.propertyvista.backend.helper.BucketNames;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(1)
public class MinioInitializer implements ApplicationRunner {
    private final MinioClient minioClient;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (Map.Entry<String, Boolean> bucketEntry : BucketNames.getAllBuckets().entrySet()) {
            String bucketName = bucketEntry.getKey();
            Boolean isPublic = bucketEntry.getValue();
            try {
                boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
                if (!found) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                    log.info("Created missing MinIO bucket: {}", bucketName);

                    // Apply public read policy if needed
                    if (isPublic) {
                        String policyJson = """
                                {
                                  "Version": "2012-10-17",
                                  "Statement": [
                                    {
                                      "Effect": "Allow",
                                      "Principal": {
                                        "AWS": [
                                          "*"
                                        ]
                                      },
                                      "Action": [
                                        "s3:GetObject"
                                      ],
                                      "Resource": [
                                        "arn:aws:s3:::%s/*"
                                      ]
                                    }
                                  ]
                                }
                                """.formatted(bucketName);
                        minioClient.setBucketPolicy(
                                SetBucketPolicyArgs.builder()
                                        .bucket(bucketName)
                                        .config(policyJson)
                                        .build()
                        );
                        log.info("Applied public read policy to MinIO bucket: {}", bucketName);
                    }
                }
            } catch (MinioException e) {
                log.error("Error while checking/creating MinIO bucket: {}", bucketName, e);
            }
        }
    }
}

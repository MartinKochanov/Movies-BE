package com.mk.movies.infrastructure.minio;

import com.mk.movies.infrastructure.exceptions.MinioInitializationException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {

    private final MinioClient minioClient;

    private static final String[] BUCKETS = {
        "movie-posters",
        "movie-trailers",
        "movie-crew-images"
    };

    @PostConstruct
    public void initializeBuckets() {
        try {
            for (String bucket : BUCKETS) {
                boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucket).build());
                if (!exists) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                    log.info("✅ Bucket created: {}", bucket);
                } else {
                    log.info("✅ Bucket already exists: {}", bucket);
                }
            }
        } catch (Exception e) {
            log.error("❌ Error initializing MinIO buckets", e);
            throw new MinioInitializationException("❌ Error initializing MinIO buckets", e);
        }
    }
}

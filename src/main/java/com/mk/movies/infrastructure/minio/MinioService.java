package com.mk.movies.infrastructure.minio;

import static com.mk.movies.infrastructure.minio.MinioPolicyUtil.getPublicReadPolicy;

import com.mk.movies.infrastructure.exceptions.MinioInitializationException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import jakarta.annotation.PostConstruct;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {

    private static final String URL = "http://localhost:9000";
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
                    setPublicReadPolicy(bucket);
                } else {
                    log.info("✅ Bucket already exists: {}", bucket);
                }
            }
        } catch (Exception e) {
            log.error("❌ Error initializing MinIO buckets", e);
            throw new MinioInitializationException("❌ Error initializing MinIO buckets", e);
        }
    }

    public String uploadFile(String bucket, MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );

            return getObjectUrl(bucket, fileName);
        } catch (Exception e) {
            throw new RuntimeException("❌ Error uploading file to MinIO", e);
        }
    }

    private String getObjectUrl(String bucket, String fileName) {
        return URL + "/" + bucket + "/" + fileName;
    }

    private void setPublicReadPolicy(String bucketName) {
        try {
            String policy = getPublicReadPolicy(bucketName);

            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                .bucket(bucketName)
                .config(policy)
                .build());

            log.info("✅ Public read policy applied for bucket: {}", bucketName);
        } catch (Exception e) {
            log.error("❌ Error setting public policy for bucket: {}", bucketName, e);
            throw new MinioInitializationException(
                "❌ Error setting public policy for bucket: " + bucketName, e);
        }
    }

    public void deleteFile(String bucketName, String fileName) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build()
            );
            log.info("✅ Successfully deleted file: {}", fileName);
        } catch (Exception e) {
            throw new RuntimeException("❌ Error deleting file from MinIO", e);
        }
    }
}

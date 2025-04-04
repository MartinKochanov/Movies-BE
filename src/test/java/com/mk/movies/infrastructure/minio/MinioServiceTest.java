package com.mk.movies.infrastructure.minio;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mk.movies.infrastructure.exceptions.MinioInitializationException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class MinioServiceTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private MinioService minioService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void initializeBuckets_createsBuckets_whenTheyDoNotExist() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);

        minioService.initializeBuckets();

        verify(minioClient, times(3)).makeBucket(any(MakeBucketArgs.class));
        verify(minioClient, times(3)).setBucketPolicy(any(SetBucketPolicyArgs.class));
    }

    @Test
    void initializeBuckets_doesNotCreateBuckets_whenTheyExist() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);

        minioService.initializeBuckets();

        verify(minioClient, never()).makeBucket(any(MakeBucketArgs.class));
        verify(minioClient, never()).setBucketPolicy(any(SetBucketPolicyArgs.class));
    }

    @Test
    void initializeBuckets_throwsMinioInitializationException_onError() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenThrow(
            new RuntimeException());

        assertThrows(MinioInitializationException.class, () -> minioService.initializeBuckets());
    }

    @Test
    void uploadFile_returnsUrl_whenFileIsUploaded() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "file.txt", "text/plain",
            "content".getBytes());

        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(null);

        String result = minioService.uploadFile("bucket", file);

        assertTrue(result.startsWith("http://localhost:9000/bucket/"));
    }

    @Test
    void uploadFile_throwsRuntimeException_onError() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "file.txt", "text/plain",
            "content".getBytes());

        doThrow(new RuntimeException()).when(minioClient).putObject(any(PutObjectArgs.class));

        assertThrows(RuntimeException.class, () -> minioService.uploadFile("bucket", file));
    }

    @Test
    void deleteFile_deletesFile_whenFileExists() throws Exception {
        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));

        minioService.deleteFile("bucket", "file.txt");

        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    void deleteFile_throwsRuntimeException_onError() throws Exception {
        doThrow(new RuntimeException()).when(minioClient).removeObject(any(RemoveObjectArgs.class));

        assertThrows(RuntimeException.class, () -> minioService.deleteFile("bucket", "file.txt"));
    }
}
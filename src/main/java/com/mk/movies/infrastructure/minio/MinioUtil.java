package com.mk.movies.infrastructure.minio;

public class MinioUtil {

    public static String getPublicReadPolicy(String bucketName) {
        return """
        {
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Effect": "Allow",
                    "Principal": "*",
                    "Action": "s3:GetObject",
                    "Resource": "arn:aws:s3:::%s/*"
                }
            ],
            "CORSRules": [
                {
                    "AllowedOrigins": ["http://localhost:5173"],
                    "AllowedMethods": ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
                    "AllowedHeaders": ["*"],
                    "ExposeHeaders": ["ETag", "x-amz-meta-custom-header"],
                    "MaxAgeSeconds": 3000
                }
            ]
        }
        """.formatted(bucketName);
    }

    public static String extractFileName(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }
}

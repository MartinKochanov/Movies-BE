package com.mk.movies.infrastructure.minio;

public class MinioPolicyUtil {

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
                ]
            }
            """.formatted(bucketName);
    }
}

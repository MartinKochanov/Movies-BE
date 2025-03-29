package com.mk.movies.infrastructure.exceptions;

public class MinioInitializationException extends RuntimeException {

    public MinioInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}

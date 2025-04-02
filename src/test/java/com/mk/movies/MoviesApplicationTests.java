package com.mk.movies;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class MoviesApplicationTests {

    @Autowired
    private MongoDBContainer mongoDBContainer;

    @Test
    void contextLoads() {
        System.out.println("MongoDBContainer: " + mongoDBContainer.isRunning());
    }
}
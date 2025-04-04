package com.mk.movies.config;

import static org.mockito.Mockito.mock;

import com.mk.movies.infrastructure.minio.MinioService;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.testcontainers.containers.MongoDBContainer;

@Configuration
public class TestContainersConfig {

    @Bean
    @ServiceConnection
    public MongoDBContainer mongoDBContainer() {
        MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.6");
        mongoDBContainer.start();
        return mongoDBContainer;
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDBContainer mongoDBContainer) {
        String uri = mongoDBContainer.getReplicaSetUrl();
        MongoDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(uri);
        return new MongoTemplate(factory);
    }

    @Bean
    public MinioService minioService() {
        return mock(MinioService.class);
    }
}
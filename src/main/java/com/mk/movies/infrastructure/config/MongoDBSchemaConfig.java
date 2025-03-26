package com.mk.movies.infrastructure.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MongoDBSchemaConfig {

    private final MongoClient mongoClient;


    @EventListener(ApplicationReadyEvent.class)
    public void applySchemaValidation() {
        MongoDatabase database = mongoClient.getDatabase("myMovieDB");

        Document jsonSchema = new Document("$jsonSchema",
            new Document("bsonType", "object")
                .append("required",
                    List.of("_id", "title", "duration", "releaseYear", "genres", "plot",
                        "filmStudio",
                        "castIds", "directedById", "producerId", "writersIds"))
                .append("properties", new Document()
                    .append("_id", new Document("bsonType", "objectId").append("description",
                        "Must be a valid ObjectId"))
                    .append("title", new Document("bsonType", "string").append("description",
                        "Title is required"))
                    .append("duration", new Document("bsonType", "int").append("minimum", 1)
                        .append("description", "Duration must be at least 1 minute"))
                    .append("releaseYear",
                        new Document("bsonType", "int").append("minimum", 1900)
                            .append("description", "Release year must be a valid year"))
                    .append("genres", new Document("bsonType", "array")
                        .append("items", new Document("bsonType", "string"))
                        .append("minItems", 1)
                        .append("description", "At least one genre must be provided"))
                    .append("plot", new Document("bsonType", "string").append("description",
                        "Plot is required"))
                    .append("imageUrl", new Document("bsonType", "string"))
                    .append("trailerUrl", new Document("bsonType", "string"))
                    .append("filmStudio",
                        new Document("bsonType", "string").append("description",
                            "Film studio is required"))
                    .append("castIds", new Document("bsonType", "array")
                        .append("items", new Document("bsonType", "string"))
                        .append("description", "Cast is required"))
                    .append("series", new Document("bsonType", "bool"))
                    .append("directedById",
                        new Document("bsonType", "string").append("description",
                            "Director is required"))
                    .append("basedOn", new Document("bsonType", "string"))
                    .append("producerId",
                        new Document("bsonType", "string").append("description",
                            "Producer is required"))
                    .append("writersIds", new Document("bsonType", "array")
                        .append("items", new Document("bsonType", "string"))
                        .append("description", "Writer is required"))
                )
        );

        database.runCommand(new Document("collMod", "Movies")
            .append("validator", jsonSchema)
            .append("validationLevel", "strict")
        );

        log.info("Schema validation applied to Movies collection");
    }
}
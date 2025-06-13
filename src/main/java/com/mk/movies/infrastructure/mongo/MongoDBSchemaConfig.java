package com.mk.movies.infrastructure.mongo;

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

        applyMoviesSchema(database);
        applyMovieCrewMemberSchema(database);

        log.info("Schema validation applied to Movies and MovieCrewMembers collections");
    }

    private void applyMoviesSchema(MongoDatabase database) {
        Document moviesJsonSchema = new Document("$jsonSchema",
            new Document("bsonType", "object")
                .append("required", List.of("_id", "title", "duration", "releaseYear", "genres",
                    "plot", "tagline","filmStudio", "castIds", "directedByIds", "producersIds", "writersIds",
                    "imageUrl", "trailerUrl"))
                .append("properties", new Document()
                    .append("_id", new Document("bsonType", "objectId").append("description",
                        "Must be a valid ObjectId"))
                    .append("title", new Document("bsonType", "string").append("description",
                        "Title is required"))
                    .append("duration", new Document("bsonType", "int")
                        .append("minimum", 1)
                        .append("description", "Duration must be at least 1 minute"))
                    .append("releaseYear", new Document("bsonType", "int")
                        .append("minimum", 1900)
                        .append("description", "Release year must be a valid year"))
                    .append("genres", new Document("bsonType", "array")
                        .append("items", new Document("bsonType", "string"))
                        .append("minItems", 1)
                        .append("description", "At least one genre must be provided"))
                    .append("plot", new Document("bsonType", "string").append("description",
                        "Plot is required"))
                    .append("tagline", new Document("bsonType", "string")
                        .append("description", "Tagline is required"))
                    .append("imageUrl", new Document("bsonType", "string")
                        .append("pattern", "^(http|https)://.*$")
                        .append("description", "Must be a valid URL"))
                    .append("trailerUrl", new Document("bsonType", "string")
                        .append("pattern", "^(http|https)://.*$")
                        .append("description", "Must be a valid URL"))
                    .append("filmStudio", new Document("bsonType", "string")
                        .append("description", "Film studio is required"))
                    .append("castIds", new Document("bsonType", "array")
                        .append("items", new Document("bsonType", "objectId"))
                        .append("minItems", 1)
                        .append("description", "At least one cast member is required"))
                    .append("series", new Document("bsonType", "bool"))
                    .append("directedByIds", new Document("bsonType", "array")
                        .append("items", new Document("bsonType", "objectId"))
                        .append("minItems", 1)
                        .append("description", "At least one director must be provided"))
                    .append("basedOn", new Document("bsonType", "string"))
                    .append("producersIds", new Document("bsonType", "array")
                        .append("items", new Document("bsonType", "objectId"))
                        .append("minItems", 1)
                        .append("description", "At least one producer must be provided"))
                    .append("writersIds", new Document("bsonType", "array")
                        .append("items", new Document("bsonType", "objectId"))
                        .append("minItems", 1)
                        .append("description", "At least one writer is required"))
                )
        );

        database.runCommand(new Document("collMod", "Movies")
            .append("validator", moviesJsonSchema)
            .append("validationLevel", "strict")
        );

        log.info("Schema validation applied to Movies collection");
    }

    private void applyMovieCrewMemberSchema(MongoDatabase database) {
        Document movieCrewMemberJsonSchema = new Document("$jsonSchema",
            new Document("bsonType", "object")
                .append("required", List.of("_id", "firstName", "lastName", "imageUrl"))
                .append("properties", new Document()
                    .append("_id", new Document("bsonType", "objectId")
                        .append("description", "Must be a valid ObjectId"))
                    .append("firstName", new Document("bsonType", "string")
                        .append("minLength", 2)
                        .append("description", "First name must be at least 2 characters long"))
                    .append("lastName", new Document("bsonType", "string")
                        .append("minLength", 2)
                        .append("description", "Last name must be at least 2 characters long"))
                    .append("imageUrl", new Document("bsonType", "string")
                        .append("pattern", "^(http|https)://.*$"))
                )
        );

        database.runCommand(new Document("collMod", "MovieCrewMembers")
            .append("validator", movieCrewMemberJsonSchema)
            .append("validationLevel", "strict")
        );

        log.info("Schema validation applied to MovieCrewMembers collection");
    }
}

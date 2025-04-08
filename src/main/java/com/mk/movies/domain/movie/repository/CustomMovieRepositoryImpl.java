package com.mk.movies.domain.movie.repository;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;

import com.mk.movies.domain.movie.dto.MovieDetailsView;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomMovieRepositoryImpl implements CustomMovieRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<MovieDetailsView> findMovieDetailsViewById(ObjectId id) {
        var aggregation = newAggregation(
            match(Criteria.where("_id").is(id)),

            lookup("MovieCrewMembers", "castIds", "_id", "cast"),
            lookup("Roles", "cast.rolesIds", "_id", "castRoles"),
            unwind("cast", true),
            unwind("castRoles", true),
            match(Criteria.where("castRoles.movieId").is(id)), // Filter roles by movieId
            group("cast._id")
                .first("cast.firstName").as("firstName")
                .first("cast.lastName").as("lastName")
                .first("cast.imageUrl").as("imageUrl")
                .push("castRoles.name").as("roles"), // Collect role names
            project("firstName", "lastName", "imageUrl", "roles"),

            // Repeat similar steps for directedBy, producers, and writers if needed
            lookup("MovieCrewMembers", "directedByIds", "_id", "directedBy"),
            lookup("MovieCrewMembers", "producersIds", "_id", "producers"),
            lookup("MovieCrewMembers", "writersIds", "_id", "writers"),

            project(
                "id", "title", "duration", "releaseYear",
                "genres", "plot", "imageUrl", "trailerUrl", "filmStudio",
                "basedOn", "series", "cast", "directedBy", "producers", "writers"
            )
        );

        AggregationResults<MovieDetailsView> results = mongoTemplate
            .aggregate(aggregation, "Movies", MovieDetailsView.class);

        return Optional.ofNullable(results.getUniqueMappedResult());
    }

}

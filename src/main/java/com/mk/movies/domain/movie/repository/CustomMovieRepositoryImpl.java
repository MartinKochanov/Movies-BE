package com.mk.movies.domain.movie.repository;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import com.mk.movies.domain.movie.document.Movie;
import com.mk.movies.domain.movie.dto.MovieDetailsView;
import com.mk.movies.domain.movie.dto.MovieFilter;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomMovieRepositoryImpl implements CustomMovieRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<MovieDetailsView> findMovieDetailsViewById(ObjectId id) {

        AggregationOperation castLookupWithRoles = context -> new Document("$lookup", new Document()
            .append("from", "MovieCrewMembers")
            .append("let", new Document("castIds", "$castIds"))
            .append("pipeline", List.of(
                new Document("$match",
                    new Document("$expr", new Document("$in", List.of("$_id", "$$castIds")))),
                new Document("$lookup", new Document()
                    .append("from", "Roles")
                    .append("localField", "_id")
                    .append("foreignField", "castId")
                    .append("as", "roles")),
                new Document("$unwind",
                    new Document("path", "$roles").append("preserveNullAndEmptyArrays", true)),
                new Document("$match", new Document("roles.movieId", id)),
                new Document("$group", new Document("_id", "$_id")
                    .append("firstName", new Document("$first", "$firstName"))
                    .append("lastName", new Document("$first", "$lastName"))
                    .append("imageUrl", new Document("$first", "$imageUrl"))
                    .append("role", new Document("$first", new Document()
                        .append("id", "$roles._id") // Ensure the role ID is included
                        .append("name", "$roles.name")))),
                new Document("$project", new Document("id", "$_id")
                    .append("firstName", 1)
                    .append("lastName", 1)
                    .append("imageUrl", 1)
                    .append("role", new Document()
                        .append("_id", "$role.id") // Explicitly project the role ID
                        .append("name", "$role.name")))
            ))
            .append("as", "cast")
        );
        var aggregation = newAggregation(
            match(Criteria.where("_id").is(id)),
            castLookupWithRoles,
            lookup("MovieCrewMembers", "directedByIds", "_id", "directedBy"),
            lookup("MovieCrewMembers", "producersIds", "_id", "producers"),
            lookup("MovieCrewMembers", "writersIds", "_id", "writers")
        );

        AggregationResults<MovieDetailsView> results = mongoTemplate
            .aggregate(aggregation, "Movies", MovieDetailsView.class);

        return Optional.ofNullable(results.getUniqueMappedResult());
    }

    @Override
    public Page<Movie> findAll(MovieFilter filter, Pageable pageable) {
        var query = new Query();

        if (filter.getTitle() != null && !filter.getTitle().isEmpty()) {
            query.addCriteria(Criteria.where("title").regex(filter.getTitle().trim(), "i"));
        }

        if (filter.getGenres() != null && !filter.getGenres().isEmpty()) {
            query.addCriteria(Criteria.where("genres").in(filter.getGenres()));
        }

        if (filter.getReleaseYear() != null) {
            query.addCriteria(Criteria.where("releaseYear").is(filter.getReleaseYear()));
        }

        if (filter.getIsSeries() != null) {
            query.addCriteria(Criteria.where("series").is(filter.getIsSeries()));
        }

        long total = mongoTemplate.count(query, Movie.class);

        query.with(pageable);

        List<Movie> movies = mongoTemplate.find(query, Movie.class);

        return new PageImpl<>(movies, pageable, total);
    }
}

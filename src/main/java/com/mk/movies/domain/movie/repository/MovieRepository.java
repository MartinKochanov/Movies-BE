package com.mk.movies.domain.movie.repository;

import com.mk.movies.domain.movie.document.Movie;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovieRepository extends MongoRepository<Movie, ObjectId> {

}

package com.mk.movies.domain.movie.repository;

import com.mk.movies.domain.movie.dto.MovieDetailsView;
import java.util.Optional;
import org.bson.types.ObjectId;

public interface CustomMovieRepository {

    Optional<MovieDetailsView> findMovieDetailsViewById(ObjectId id);
}

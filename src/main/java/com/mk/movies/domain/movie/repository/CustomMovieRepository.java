package com.mk.movies.domain.movie.repository;

import com.mk.movies.domain.movie.document.Movie;
import com.mk.movies.domain.movie.dto.MovieDetailsView;
import com.mk.movies.domain.movie.dto.MovieFilter;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomMovieRepository {

    Optional<MovieDetailsView> findMovieDetailsViewById(ObjectId id);

    Page<Movie> findAll(MovieFilter filter, Pageable pageable);
}

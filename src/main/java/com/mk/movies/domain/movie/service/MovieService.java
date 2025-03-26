package com.mk.movies.domain.movie.service;

import static com.mk.movies.infrastructure.util.ObjectIdUtil.validateObjectId;

import com.mk.movies.domain.movie.document.Movie;
import com.mk.movies.domain.movie.dto.MovieDetailsView;
import com.mk.movies.domain.movie.dto.MovieRequest;
import com.mk.movies.domain.movie.dto.MovieSimpleView;
import com.mk.movies.domain.movie.dto.MovieUpdateRequest;
import com.mk.movies.domain.movie.mappers.MovieMapper;
import com.mk.movies.domain.movie.repository.MovieRepository;
import com.mk.movies.infrastructure.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public MovieDetailsView create(MovieRequest dto) {
        var movie = movieMapper.toDocument(dto);
        return movieMapper.toDetailsView(movieRepository.save(movie));
    }

    public Page<MovieSimpleView> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable).map(movieMapper::toSimpleView);
    }

    public MovieDetailsView getMovieById(String id) {
        validateObjectId(id);
        return movieMapper.toDetailsView(getMovie(new ObjectId(id)));
    }

    public MovieDetailsView updateMovie(String id, MovieUpdateRequest request) {
        validateObjectId(id);
        var movie = getMovie(new ObjectId(id));

        movieMapper.updateDocument(request, movie);

        movieRepository.save(movie);

        return movieMapper.toDetailsView(movie);
    }

    public void deleteMovie(String id) {
        validateObjectId(id);
        var objectId = new ObjectId(id);
        validateMovieExists(objectId);
        movieRepository.deleteById(objectId);
    }

    private void validateMovieExists(ObjectId id) {
        if (!movieRepository.existsById(id)) {
            throw new ResourceNotFoundException("Movie with id " + id + " not found");
        }
    }

    private Movie getMovie(ObjectId id) {
        return movieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Movie with id " + id + " not found"));
    }
}


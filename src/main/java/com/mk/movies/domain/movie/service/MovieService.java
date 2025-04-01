package com.mk.movies.domain.movie.service;

import static com.mk.movies.infrastructure.util.MinioConstants.MOVIE_POSTERS_BUCKET;
import static com.mk.movies.infrastructure.util.MinioConstants.MOVIE_TRAILERS_BUCKET;
import static com.mk.movies.infrastructure.util.ObjectIdUtil.validateObjectId;

import com.mk.movies.domain.movie.document.Movie;
import com.mk.movies.domain.movie.dto.MovieDetailsView;
import com.mk.movies.domain.movie.dto.MovieRequest;
import com.mk.movies.domain.movie.dto.MovieSimpleView;
import com.mk.movies.domain.movie.dto.MovieUpdateRequest;
import com.mk.movies.domain.movie.repository.MovieRepository;
import com.mk.movies.infrastructure.exceptions.ResourceNotFoundException;
import com.mk.movies.infrastructure.mappers.MovieMapper;
import com.mk.movies.infrastructure.minio.MinioService;
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
    private final MinioService minioService;

    public MovieDetailsView create(MovieRequest movieRequest) {
        var imageUrl = minioService.uploadFile(MOVIE_POSTERS_BUCKET, movieRequest.imageUrl());
        var trailerUrl = minioService.uploadFile(MOVIE_TRAILERS_BUCKET, movieRequest.trailerUrl());

        var movie = movieMapper.toDocument(movieRequest);
        movie.setImageUrl(imageUrl);
        movie.setTrailerUrl(trailerUrl);

        movieRepository.save(movie);
        return getMovieDetailsViewById(movie.getId());
    }

    public Page<MovieSimpleView> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable).map(movieMapper::toSimpleView);
    }

    public MovieDetailsView getMovieById(String id) {
        validateObjectId(id);
        return getMovieDetailsViewById(new ObjectId(id));
    }

    public MovieDetailsView updateMovie(String id, MovieUpdateRequest movieUpdateRequest) {
        validateObjectId(id);
        var movie = getMovie(new ObjectId(id));

        var imageUrl = movieUpdateRequest.imageUrl() != null
            ? minioService.uploadFile(MOVIE_POSTERS_BUCKET, movieUpdateRequest.imageUrl())
            : movie.getImageUrl();

        var trailerUrl = movieUpdateRequest.trailerUrl() != null
            ? minioService.uploadFile(MOVIE_TRAILERS_BUCKET, movieUpdateRequest.trailerUrl())
            : movie.getTrailerUrl();

        movie.setImageUrl(imageUrl);
        movie.setTrailerUrl(trailerUrl);
        movieMapper.updateDocument(movieUpdateRequest, movie);

        movieRepository.save(movie);
        return getMovieDetailsViewById(movie.getId());
    }

    public void deleteMovie(String id) {
        validateObjectId(id);
        var objectId = new ObjectId(id);
        var movie = getMovie(objectId);

        String imageName = movie.getImageUrl()
            .substring(movie.getImageUrl().lastIndexOf("/") + 1);

        String trailerName = movie.getTrailerUrl()
            .substring(movie.getTrailerUrl().lastIndexOf("/") + 1);

        minioService.deleteFile(MOVIE_POSTERS_BUCKET, imageName);
        minioService.deleteFile(MOVIE_TRAILERS_BUCKET, trailerName);
        movieRepository.deleteById(objectId);
    }

    private Movie getMovie(ObjectId id) {
        return movieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Movie with id " + id + " not found"));
    }

    private MovieDetailsView getMovieDetailsViewById(ObjectId id) {
        return movieRepository.findMovieDetailsViewById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Movie with id " + id + " not found"));
    }
}


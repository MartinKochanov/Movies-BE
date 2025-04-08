package com.mk.movies.domain.movie.service;

import static com.mk.movies.infrastructure.minio.MinioConstants.MOVIE_POSTERS_BUCKET;
import static com.mk.movies.infrastructure.minio.MinioConstants.MOVIE_TRAILERS_BUCKET;
import static com.mk.movies.infrastructure.minio.MinioUtil.extractFileName;
import static com.mk.movies.infrastructure.util.ObjectIdUtil.validateObjectId;

import com.mk.movies.domain.movie.document.Movie;
import com.mk.movies.domain.movie.dto.MovieDetailsView;
import com.mk.movies.domain.movie.dto.MovieRequest;
import com.mk.movies.domain.movie.dto.MovieSimpleView;
import com.mk.movies.domain.movie.dto.MovieUpdateRequest;
import com.mk.movies.domain.movie.repository.MovieRepository;
import com.mk.movies.domain.role.dto.RoleRequest;
import com.mk.movies.domain.role.repository.RoleRepository;
import com.mk.movies.infrastructure.exceptions.ResourceNotFoundException;
import com.mk.movies.infrastructure.mappers.MovieMapper;
import com.mk.movies.infrastructure.mappers.RoleMapper;
import com.mk.movies.infrastructure.minio.MinioService;
import java.util.List;
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
    private final RoleMapper roleMapper;
    private final RoleRepository roleRepository;

    public MovieDetailsView create(MovieRequest movieRequest) {
        var imageUrl = minioService.uploadFile(MOVIE_POSTERS_BUCKET, movieRequest.imageUrl());
        var trailerUrl = minioService.uploadFile(MOVIE_TRAILERS_BUCKET, movieRequest.trailerUrl());

        var movie = movieMapper.toDocument(movieRequest);
        movie.setImageUrl(imageUrl);
        movie.setTrailerUrl(trailerUrl);

        movieRepository.save(movie);

        createRolesForMovieMembers(movieRequest.roles(), movie.getId());
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

        if (movieUpdateRequest.imageUrl() != null && !movieUpdateRequest.imageUrl().isEmpty()) {
            String oldImageName = extractFileName(movie.getImageUrl());
            minioService.deleteFile(MOVIE_POSTERS_BUCKET, oldImageName);

            var imageUrl = minioService.uploadFile(
                MOVIE_POSTERS_BUCKET,
                movieUpdateRequest.imageUrl());
            movie.setImageUrl(imageUrl);
        }

        if (movieUpdateRequest.trailerUrl() != null && !movieUpdateRequest.trailerUrl().isEmpty()) {
            String oldTrailerName = extractFileName(movie.getTrailerUrl());
            minioService.deleteFile(MOVIE_TRAILERS_BUCKET, oldTrailerName);

            var trailerUrl = minioService.uploadFile(
                MOVIE_TRAILERS_BUCKET,
                movieUpdateRequest.trailerUrl());
            movie.setTrailerUrl(trailerUrl);
        }

        movieMapper.updateDocument(movieUpdateRequest, movie);
        movieRepository.save(movie);
        return getMovieDetailsViewById(movie.getId());
    }

    public void deleteMovie(String id) {
        validateObjectId(id);
        var objectId = new ObjectId(id);
        var movie = getMovie(objectId);

        String imageName = extractFileName(movie.getImageUrl());
        String trailerName = extractFileName(movie.getTrailerUrl());

        minioService.deleteFile(MOVIE_POSTERS_BUCKET, imageName);
        minioService.deleteFile(MOVIE_TRAILERS_BUCKET, trailerName);
        movieRepository.deleteById(objectId);
    }

    public Movie getMovie(ObjectId id) {
        return movieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Movie with id " + id + " not found"));
    }

    public MovieDetailsView getMovieDetailsViewById(ObjectId id) {
        return movieRepository.findMovieDetailsViewById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Movie with id " + id + " not found"));
    }

    private void createRolesForMovieMembers(List<RoleRequest> roles, ObjectId movieId) {
        for (var roleRequest : roles) {
            var role = roleMapper.toDocument(roleRequest);
            role.setMovieId(movieId);
            roleRepository.save(role);
        }
    }
}


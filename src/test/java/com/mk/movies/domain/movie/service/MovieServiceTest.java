package com.mk.movies.domain.movie.service;

import static com.mk.movies.infrastructure.minio.MinioConstants.MOVIE_POSTERS_BUCKET;
import static com.mk.movies.infrastructure.minio.MinioConstants.MOVIE_TRAILERS_BUCKET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mk.movies.domain.movie.document.Movie;
import com.mk.movies.domain.movie.dto.MovieDetailsView;
import com.mk.movies.domain.movie.dto.MovieRequest;
import com.mk.movies.domain.movie.dto.MovieSimpleView;
import com.mk.movies.domain.movie.dto.MovieUpdateRequest;
import com.mk.movies.domain.movie.enums.Genre;
import com.mk.movies.domain.movie.repository.MovieRepository;
import com.mk.movies.domain.role.document.Role;
import com.mk.movies.domain.role.dto.RoleRequest;
import com.mk.movies.domain.role.repository.RoleRepository;
import com.mk.movies.infrastructure.exceptions.ResourceNotFoundException;
import com.mk.movies.infrastructure.mappers.MovieMapper;
import com.mk.movies.infrastructure.mappers.RoleMapper;
import com.mk.movies.infrastructure.minio.MinioService;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MinioService minioService;

    @Mock
    RoleMapper roleMapper;

    @Mock
    private MovieMapper movieMapper;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie movie;
    private MovieRequest movieRequest;
    private MovieUpdateRequest movieUpdateRequest;
    private MovieDetailsView movieDetailsView;
    private MovieSimpleView movieSimpleView;
    private RoleRequest roleRequest;
    private Role role;

    @BeforeEach
    void setUp() {

        roleRequest = new RoleRequest(
            "Role",
            new ObjectId(),
            new ObjectId()
        );

        role = new Role();
        role.setId(new ObjectId());
        role.setName("Role");
        role.setCastId(new ObjectId());
        role.setMovieId(new ObjectId());

        when(roleMapper.toDocument(roleRequest)).thenReturn(role);

        movie = new Movie();
        movie.setId(new ObjectId());
        movie.setTitle("Title");
        movie.setDuration(120);
        movie.setReleaseYear(2023);
        movie.setGenres(List.of(Genre.ACTION));
        movie.setPlot("Plot");
        movie.setImageUrl("http://localhost:8080/image.jpg");
        movie.setTrailerUrl("http://localhost:8080/trailer.mp4");
        movie.setFilmStudio("Film Studio");
        movie.setSeries(false);
        movie.setBasedOn("True Story");
        movie.setCastIds(List.of(new ObjectId()));
        movie.setDirectedByIds(List.of(new ObjectId()));
        movie.setProducersIds(List.of(new ObjectId()));
        movie.setWritersIds(List.of(new ObjectId()));

        movieRequest = new MovieRequest(
            "Title",
            120,
            2023,
            List.of(Genre.ACTION),
            "Plot",
            new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[0]),
            new MockMultipartFile("trailer", "trailer.mp4", "video/mp4", new byte[0]),
            "Film Studio",
            "True Story",
            false,
            List.of(new ObjectId()),
            List.of(new ObjectId()),
            List.of(new ObjectId()),
            List.of(new ObjectId()),
            List.of(roleRequest)
        );

        movieUpdateRequest = new MovieUpdateRequest(
            "Updated Title",
            130,
            2024,
            List.of(Genre.DRAMA),
            "Updated Plot",
            new MockMultipartFile("image", "updated_image.jpg", "image/jpeg", new byte[0]),
            new MockMultipartFile("trailer", "updated_trailer.mp4", "video/mp4", new byte[0]),
            "Updated Film Studio",
            "Updated True Story",
            true,
            List.of(new ObjectId()),
            List.of(new ObjectId()),
            List.of(new ObjectId()),
            List.of(new ObjectId())
        );

        movieDetailsView = new MovieDetailsView(
            movie.getId().toHexString(),
            "Title",
            120,
            2023,
            List.of(Genre.ACTION),
            "Plot",
            "http://localhost:8080/image.jpg",
            "http://localhost:8080/trailer.mp4",
            "Film Studio",
            "True Story",
            false,
            List.of(),
            List.of(),
            List.of(),
            List.of()
        );
        // Use lenient stubbing to avoid UnnecessaryStubbingException
        lenient().when(roleMapper.toDocument(roleRequest)).thenReturn(role);

        movieSimpleView = new MovieSimpleView(
            movie.getId().toHexString(),
            "Title",
            120,
            2023,
            List.of(Genre.ACTION),
            "http://localhost:8080/image.jpg"
        );
    }

    @Test
void create_returnsMovieDetailsView_whenMovieIsSaved() {

    when(movieMapper.toDocument(movieRequest)).thenReturn(movie);
    when(minioService.uploadFile(MOVIE_POSTERS_BUCKET, movieRequest.imageUrl()))
        .thenReturn(movie.getImageUrl());
    when(minioService.uploadFile(MOVIE_TRAILERS_BUCKET, movieRequest.trailerUrl()))
        .thenReturn(movie.getTrailerUrl());
    when(roleMapper.toDocument(roleRequest)).thenReturn(role);
    when(movieRepository.save(movie)).thenReturn(movie);
    when(movieRepository.findMovieDetailsViewById(movie.getId()))
        .thenReturn(Optional.of(movieDetailsView));

    var result = movieService.create(movieRequest);

    assertEquals(movieDetailsView, result);
    verify(movieRepository).save(movie);
    verify(minioService).uploadFile(MOVIE_POSTERS_BUCKET, movieRequest.imageUrl());
    verify(minioService).uploadFile(MOVIE_TRAILERS_BUCKET, movieRequest.trailerUrl());
    verify(movieMapper).toDocument(movieRequest);
    verify(movieRepository).findMovieDetailsViewById(movie.getId());
    verify(roleMapper).toDocument(roleRequest);
    verify(roleRepository).save(role); // Ensure this is invoked
}

    @Test
    void getAllMovies_returnsPageOfMovieSimpleView() {
        Page<Movie> moviePage = new PageImpl<>(List.of(movie));
        when(movieRepository.findAll(PageRequest.of(0, 10))).thenReturn(moviePage);
        when(movieMapper.toSimpleView(movie)).thenReturn(movieSimpleView);

        var result = movieService.getAllMovies(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(movieSimpleView, result.getContent().get(0));
    }

    @Test
    void getMovieById_returnsMovieDetailsView_whenMovieExists() {
        when(movieRepository.findMovieDetailsViewById(movie.getId())).thenReturn(
            Optional.of(movieDetailsView));

        var result = movieService.getMovieById(movie.getId());

        assertEquals(movieDetailsView, result);
    }

    @Test
    void getMovieById_throwsResourceNotFoundException_whenMovieDoesNotExist() {
        when(movieRepository.findMovieDetailsViewById(movie.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> movieService.getMovieById(movie.getId()));
    }

    @Test
    void updateMovie_returnsUpdatedMovieDetailsView_whenMovieIsUpdated() {
        when(movieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));
        when(movieRepository.findMovieDetailsViewById(movie.getId())).thenReturn(
            Optional.of(movieDetailsView));

        var result = movieService.updateMovie(movie.getId(), movieUpdateRequest);

        assertEquals(movieDetailsView, result);
    }

    @Test
    void deleteMovie_deletesMovie_whenMovieExists() {
        when(movieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));

        movieService.deleteMovie(movie.getId());

        verify(movieRepository).deleteById(movie.getId());
        verify(minioService).deleteFile(MOVIE_POSTERS_BUCKET, "image.jpg");
        verify(minioService).deleteFile(MOVIE_TRAILERS_BUCKET, "trailer.mp4");
    }

    @Test
    void deleteMovie_throwsResourceNotFoundException_whenMovieDoesNotExist() {
        when(movieRepository.findById(movie.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> movieService.deleteMovie(movie.getId()));
    }
}
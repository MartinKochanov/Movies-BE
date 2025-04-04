package com.mk.movies.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mk.movies.domain.movie.document.Movie;
import com.mk.movies.domain.movie.dto.MovieRequest;
import com.mk.movies.domain.movie.dto.MovieUpdateRequest;
import com.mk.movies.domain.movie.enums.Genre;
import com.mk.movies.domain.movie.repository.MovieRepository;
import com.mk.movies.infrastructure.minio.MinioService;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MovieControllerTest {

    private static final String BASE_URL = "/movies";
    private static Movie movie;
    private static MovieRequest movieRequest;
    private static MovieUpdateRequest movieUpdateRequest;
    @Mock
    MinioService minioService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MovieRepository movieRepository;

    @BeforeEach
    void setUp() {

        movieRequest = new MovieRequest(
            "Title",
            120,
            2023,
            List.of(Genre.ACTION),
            "Plot",
            new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[0]),
            new MockMultipartFile("trailer", "trailer.mp4", "video/mp4", new byte[0]),
            "Film Studio",
            null,
            false,
            List.of(new ObjectId()),
            List.of(new ObjectId()),
            List.of(new ObjectId()),
            List.of(new ObjectId())
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
            null,
            true,
            List.of(new ObjectId()),
            List.of(new ObjectId()),
            List.of(new ObjectId()),
            List.of(new ObjectId())
        );

        movie = new Movie();
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
        movie.setId(new ObjectId());

        movieRepository.save(movie);
    }

    @Test
    void create_returns_created_givenValidData() throws Exception {
        mockMvc.perform(multipart(BASE_URL)
                .file("imageUrl", movieRequest.imageUrl().getBytes())
                .file("trailerUrl", movieRequest.trailerUrl().getBytes())
                .param("title", movieRequest.title())
                .param("duration", String.valueOf(movieRequest.duration()))
                .param("releaseYear", String.valueOf(movieRequest.releaseYear()))
                .param("genres", String.valueOf(movieRequest.genres().get(0)))
                .param("plot", movieRequest.plot())
                .param("filmStudio", movieRequest.filmStudio())
                .param("basedOn", movieRequest.basedOn())
                .param("series", String.valueOf(movieRequest.series()))
                .param("castIds", String.valueOf(movieRequest.castIds().get(0)))
                .param("directedByIds", String.valueOf(movieRequest.directedByIds().get(0)))
                .param("producersIds", String.valueOf(movieRequest.producersIds().get(0)))
                .param("writersIds", String.valueOf(movieRequest.writersIds().get(0))))
            .andExpect(status().isCreated());
    }

    @Test
    void create_returns_bad_request_givenNoData() throws Exception {
        mockMvc.perform(multipart(BASE_URL))
            .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @CsvSource({
        ", 120, 2023, 'ACTION', Plot, image.jpg, trailer.mp4, 'Film Studio', 'True Story', false, 'castId', 'directedById', 'producersId', 'writersId'",
        // Missing title
        "Title, , 2023, 'ACTION', Plot, image.jpg, trailer.mp4, 'Film Studio', 'True Story', false, 'castId', 'directedById', 'producersId', 'writersId'",
        // Missing duration
        "Title, 120, , 'ACTION', Plot, image.jpg, trailer.mp4, 'Film Studio', 'True Story', false, 'castId', 'directedById', 'producersId', 'writersId'",
        // Missing release year
        "Title, 120, 2023, , Plot, image.jpg, trailer.mp4, 'Film Studio', 'True Story', false, 'castId', 'directedById', 'producersId', 'writersId'",
        // Missing genres
        "Title, 120, 2023, 'ACTION', , image.jpg, trailer.mp4, 'Film Studio', 'True Story', false, 'castId', 'directedById', 'producersId', 'writersId'",
        // Missing plot
        "Title, 120, 2023, 'ACTION', Plot, , trailer.mp4, 'Film Studio', 'True Story', false, 'castId', 'directedById', 'producersId', 'writersId'",
        // Missing image
        "Title, 120, 2023, 'ACTION', Plot, image.jpg, , 'Film Studio', 'True Story', false, 'castId', 'directedById', 'producersId', 'writersId'",
        // Missing trailer
        "Title, 120, 2023, 'ACTION', Plot, image.jpg, trailer.mp4, , 'True Story', false, 'castId', 'directedById', 'producersId', 'writersId'",
        // Missing film studio
        "Title, 120, 2023, 'ACTION', Plot, image.jpg, trailer.mp4, 'Film Studio', 'True Story', false, '', 'directedById', 'producersId', 'writersId'",
        // Missing cast IDs
        "Title, 120, 2023, 'ACTION', Plot, image.jpg, trailer.mp4, 'Film Studio', 'True Story', false, 'castId', , 'producersId', 'writersId'",
        // Missing directed by IDs
        "Title, 120, 2023, 'ACTION', Plot, image.jpg, trailer.mp4, 'Film Studio', 'True Story', false, 'castId', 'directedById', , 'writersId'",
        // Missing producers IDs
        "Title, 120, 2023, 'ACTION', Plot, image.jpg, trailer.mp4, 'Film Studio', 'True Story', false, 'castId', 'directedById', 'producersId', ''",
        // Missing writers IDs
    })
    void create_returns_bad_request_givenInvalidData(
        String title,
        Integer duration,
        Integer releaseYear,
        String genres,
        String plot,
        String imageUrl,
        String trailerUrl,
        String filmStudio,
        String basedOn,
        Boolean series,
        String castIds,
        String directedByIds,
        String producersIds,
        String writersIds
    ) throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("imageUrl", imageUrl, "image/jpeg",
            new byte[0]);
        MockMultipartFile trailerFile = new MockMultipartFile("trailerUrl", trailerUrl, "video/mp4",
            new byte[0]);
        mockMvc.perform(multipart("/movies")
                .file("imageUrl", imageFile.getBytes())
                .file("trailerUrl", trailerFile.getBytes())
                .param("title", title)
                .param("duration", duration != null ? String.valueOf(duration) : "")
                .param("releaseYear", releaseYear != null ? String.valueOf(releaseYear) : "")
                .param("genres", genres)
                .param("plot", plot)
                .param("filmStudio", filmStudio)
                .param("basedOn", basedOn)
                .param("series", series != null ? String.valueOf(series) : "")
                .param("castIds", castIds)
                .param("directedByIds", directedByIds)
                .param("producersIds", producersIds)
                .param("writersIds", writersIds))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getAllMovies_returns_ok() throws Exception {
        mockMvc.perform(get(BASE_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].title").value(movie.getTitle()))
            .andExpect(jsonPath("$.content[0].duration").value(movie.getDuration()))
            .andExpect(jsonPath("$.content[0].releaseYear").value(movie.getReleaseYear()))
            .andExpect(
                jsonPath("$.content[0].genres[0]").value(movie.getGenres().get(0).toString()));

    }

    @Test
    void getMovieById_returns_ok_givenValidId() throws Exception {
        mockMvc.perform(get(BASE_URL + "/" + movie.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(movie.getTitle()))
            .andExpect(jsonPath("$.duration").value(movie.getDuration()))
            .andExpect(jsonPath("$.releaseYear").value(movie.getReleaseYear()))
            .andExpect(jsonPath("$.genres[0]").value(movie.getGenres().get(0).toString()))
            .andExpect(jsonPath("$.plot").value(movie.getPlot()))
            .andExpect(jsonPath("$.imageUrl").value(movie.getImageUrl()))
            .andExpect(jsonPath("$.trailerUrl").value(movie.getTrailerUrl()))
            .andExpect(jsonPath("$.filmStudio").value(movie.getFilmStudio()))
            .andExpect(jsonPath("$.series").value(movie.isSeries()))
            .andExpect(jsonPath("$.basedOn").value(movie.getBasedOn()));
    }

    @Test
    void getMovieById_returns_not_found_givenNonExistentId() throws Exception {
        mockMvc.perform(get(BASE_URL + "/" + new ObjectId().toHexString()))
            .andExpect(status().isNotFound());
    }

    @Test
    void getMovieById_returns_bad_request_givenInvalidId() throws Exception {
        mockMvc.perform(get(BASE_URL + "/invalid-id"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateMovie_returns_ok_givenValidData() throws Exception {
        mockMvc.perform(multipart(BASE_URL + "/" + movie.getId())
                .file("imageUrl", movieUpdateRequest.imageUrl().getBytes())
                .file("trailerUrl", movieUpdateRequest.trailerUrl().getBytes())
                .param("title", movieUpdateRequest.title())
                .param("duration", String.valueOf(movieUpdateRequest.duration()))
                .param("releaseYear", String.valueOf(movieUpdateRequest.releaseYear()))
                .param("genres", String.valueOf(movieUpdateRequest.genres().get(0)))
                .param("plot", movieUpdateRequest.plot())
                .param("filmStudio", movieUpdateRequest.filmStudio())
                .param("basedOn", movieUpdateRequest.basedOn())
                .param("series", String.valueOf(movieUpdateRequest.series()))
                .param("castIds", String.valueOf(movieUpdateRequest.castIds().get(0)))
                .param("directedByIds", String.valueOf(movieUpdateRequest.directedByIds().get(0)))
                .param("producersIds", String.valueOf(movieUpdateRequest.producersIds().get(0)))
                .param("writersIds", String.valueOf(movieUpdateRequest.writersIds().get(0)))
                .with(request -> {
                    request.setMethod("PATCH");
                    return request;
                }))
            .andExpect(status().isOk());
    }

    @ParameterizedTest
    @CsvSource({
        "Title, , 2023, 'ACTION', Plot, image.jpg, trailer.mp4, 'Film Studio', 'True Story', false, 'castId', 'directedById', 'producersId', 'writersId'",
        // Missing duration
        "Title, 120, , 'ACTION', Plot, image.jpg, trailer.mp4, 'Film Studio', 'True Story', false, 'castId', 'directedById', 'producersId', 'writersId'",
        // Missing release year
        "Title, 120, 2023, , Plot, image.jpg, trailer.mp4, 'Film Studio', 'True Story', false, 'castId', 'directedById', 'producersId', 'writersId'",
        // Missing genres
        "Title, 120, 2023, 'ACTION', 'Plot', image.jpg, trailer.mp4, 'Film Studio', 'True Story', false, '', 'directedById', 'producersId', 'writersId'",
        // Missing cast IDs
        "Title, 120, 2023, 'ACTION', Plot, image.jpg, trailer.mp4, 'Film Studio', 'True Story', false, 'castId', , 'producersId', 'writersId'",
        // Missing directed by IDs
        "Title, 120, 2023, 'ACTION', Plot, image.jpg, trailer.mp4, 'Film Studio', 'True Story', false, 'castId', 'directedById', , 'writersId'",
        // Missing producers IDs
        "Title, 120, 2023, 'ACTION', Plot, image.jpg, trailer.mp4, 'Film Studio', 'True Story', false, 'castId', 'directedById', 'producersId', ''",
        // Missing writers IDs
    })
    void updateMovie_returns_bad_request_givenInvalidData(
        String title,
        Integer duration,
        Integer releaseYear,
        String genres,
        String plot,
        String imageUrl,
        String trailerUrl,
        String filmStudio,
        String basedOn,
        Boolean series,
        String castIds,
        String directedByIds,
        String producersIds,
        String writersIds
    ) throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("imageUrl", imageUrl, "image/jpeg",
            new byte[0]);
        MockMultipartFile trailerFile = new MockMultipartFile("trailerUrl", trailerUrl, "video/mp4",
            new byte[0]);
        mockMvc.perform(multipart(BASE_URL + "/" + movie.getId())
                .file("imageUrl", imageFile.getBytes())
                .file("trailerUrl", trailerFile.getBytes())
                .param("title", title)
                .param("duration", duration != null ? String.valueOf(duration) : "")
                .param("releaseYear", releaseYear != null ? String.valueOf(releaseYear) : "")
                .param("genres", genres)
                .param("plot", plot)
                .param("filmStudio", filmStudio)
                .param("basedOn", basedOn)
                .param("series", series != null ? String.valueOf(series) : "")
                .param("castIds", castIds)
                .param("directedByIds", directedByIds)
                .param("producersIds", producersIds)
                .param("writersIds", writersIds)
                .with(request -> {
                    request.setMethod("PATCH");
                    return request;
                }))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateMovie_returns_not_found_givenNonExistentId() throws Exception {
        mockMvc.perform(multipart(BASE_URL + "/" + new ObjectId().toHexString())
                .file("imageUrl", movieUpdateRequest.imageUrl().getBytes())
                .file("trailerUrl", movieUpdateRequest.trailerUrl().getBytes())
                .param("title", movieUpdateRequest.title())
                .param("duration", String.valueOf(movieUpdateRequest.duration()))
                .param("releaseYear", String.valueOf(movieUpdateRequest.releaseYear()))
                .param("genres", String.valueOf(movieUpdateRequest.genres().get(0)))
                .param("plot", movieUpdateRequest.plot())
                .param("filmStudio", movieUpdateRequest.filmStudio())
                .param("basedOn", movieUpdateRequest.basedOn())
                .param("series", String.valueOf(movieUpdateRequest.series()))
                .param("castIds", String.valueOf(movieUpdateRequest.castIds().get(0)))
                .param("directedByIds", String.valueOf(movieUpdateRequest.directedByIds().get(0)))
                .param("producersIds", String.valueOf(movieUpdateRequest.producersIds().get(0)))
                .param("writersIds", String.valueOf(movieUpdateRequest.writersIds().get(0)))
                .with(request -> {
                    request.setMethod("PATCH");
                    return request;
                }))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateMovie_returns_bad_request_givenInvalidId() throws Exception {
        mockMvc.perform(multipart(BASE_URL + "/invalid-id")
                .file("imageUrl", movieUpdateRequest.imageUrl().getBytes())
                .file("trailerUrl", movieUpdateRequest.trailerUrl().getBytes())
                .param("title", movieUpdateRequest.title())
                .param("duration", String.valueOf(movieUpdateRequest.duration()))
                .param("releaseYear", String.valueOf(movieUpdateRequest.releaseYear()))
                .param("genres", String.valueOf(movieUpdateRequest.genres().get(0)))
                .param("plot", movieUpdateRequest.plot())
                .param("filmStudio", movieUpdateRequest.filmStudio())
                .param("basedOn", movieUpdateRequest.basedOn())
                .param("series", String.valueOf(movieUpdateRequest.series()))
                .param("castIds", String.valueOf(movieUpdateRequest.castIds().get(0)))
                .param("directedByIds", String.valueOf(movieUpdateRequest.directedByIds().get(0)))
                .param("producersIds", String.valueOf(movieUpdateRequest.producersIds().get(0)))
                .param("writersIds", String.valueOf(movieUpdateRequest.writersIds().get(0)))
                .with(request -> {
                    request.setMethod("PATCH");
                    return request;
                }))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deleteMovie_returns_no_content_givenValidId() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/" + movie.getId()))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteMovie_returns_not_found_givenNonExistentId() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/" + new ObjectId().toHexString()))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteMovie_returns_bad_request_givenInvalidId() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/invalid-id"))
            .andExpect(status().isBadRequest());
    }
}
package com.mk.movies.web;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.mk.movies.domain.movie.dto.MovieDetailsView;
import com.mk.movies.domain.movie.dto.MovieRequest;
import com.mk.movies.domain.movie.dto.MovieSimpleView;
import com.mk.movies.domain.movie.dto.MovieUpdateRequest;
import com.mk.movies.domain.movie.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Movies", description = "API for managing movies")
@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    @Operation(summary = "Create a movie", description = "Create a movie with the given data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Movie created"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<MovieDetailsView> create(@ModelAttribute @Valid MovieRequest movie) {
        return ResponseEntity
            .status(CREATED)
            .body(movieService.create(movie));
    }

    @Operation(summary = "Get all movies", description = "Get all movies with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movies retrieved"),
    })
    @GetMapping
    public ResponseEntity<Page<MovieSimpleView>> getAllMovies(Pageable pageable) {
        return ResponseEntity.ok(movieService.getAllMovies(pageable));
    }

    @Operation(summary = "Get movie details", description = "Retrieves detailed information about a specific movie.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movie retrieved"),
        @ApiResponse(responseCode = "400", description = "Invalid object ID"),
        @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MovieDetailsView> getMovieById(@PathVariable ObjectId id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @Operation(summary = "Update movie details", description = "Updates an existing movie. Supports partial updates.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movie updated"),
        @ApiResponse(responseCode = "400", description = "Invalid input data or object ID"),
        @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @PatchMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<MovieDetailsView> update(@PathVariable ObjectId id,
        @ModelAttribute @Valid MovieUpdateRequest movie) {
        return ResponseEntity.ok(movieService.updateMovie(id, movie));
    }

    @Operation(summary = "Delete a movie", description = "Deletes a movie by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Movie deleted"),
        @ApiResponse(responseCode = "400", description = "Invalid object ID"),
        @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable ObjectId id) {
        movieService.deleteMovie(id);
        return ResponseEntity
            .status(NO_CONTENT)
            .build();
    }
}

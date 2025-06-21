package com.mk.movies.web;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.mk.movies.domain.movie_crew_member.dto.MovieCrewMemberRequest;
import com.mk.movies.domain.movie_crew_member.dto.MovieCrewMemberUpdateRequest;
import com.mk.movies.domain.movie_crew_member.dto.MovieCrewMemberView;
import com.mk.movies.domain.movie_crew_member.service.MovieCrewMemberService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Movie Crew Members", description = "API for managing movie crew members")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movie-crew-members")
public class MovieCrewMemberController {

    private final MovieCrewMemberService movieCrewMemberService;

    @Operation(summary = "Create a movie crew member", description = "Create a movie crew member with the given data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Movie crew member created"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<MovieCrewMemberView> createMovieCrewMember(
        @ModelAttribute @Valid MovieCrewMemberRequest movieCrewMemberRequest) {
        return ResponseEntity
            .status(CREATED)
            .body(movieCrewMemberService.create(movieCrewMemberRequest));
    }

    @Operation(summary = "Get all movie crew members", description = "Get all movie crew members with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movie crew members retrieved"),
    })
    @GetMapping
    public ResponseEntity<Page<MovieCrewMemberView>> getMovieCrewMember(Pageable pageable) {
        return ResponseEntity.ok().body(movieCrewMemberService.getAll(pageable));
    }

    @Operation(summary = "Get movie crew member details", description = "Retrieves detailed information about a specific movie crew member.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movie crew member retrieved"),
        @ApiResponse(responseCode = "400", description = "Invalid object id"),
        @ApiResponse(responseCode = "404", description = "Movie crew member not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MovieCrewMemberView> getMovieCrewMemberById(@PathVariable ObjectId id) {
        return ResponseEntity.ok().body(movieCrewMemberService.getById(id));
    }

    @Operation(summary = "Update a movie crew member", description = "Update an existing movie crew member. Supports partial updates.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movie crew member updated"),
        @ApiResponse(responseCode = "400", description = "Invalid input data or object id"),
        @ApiResponse(responseCode = "404", description = "Movie crew member not found")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PatchMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<MovieCrewMemberView> updateMovieCrewMember(@PathVariable ObjectId id,
        @ModelAttribute @Valid MovieCrewMemberUpdateRequest movieCrewMemberRequest) {
        return ResponseEntity.ok().body(movieCrewMemberService.update(id, movieCrewMemberRequest));
    }

    @Operation(summary = "Delete a movie", description = "Deletes a movie by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Movie crew member deleted"),
        @ApiResponse(responseCode = "400", description = "Invalid object id"),
        @ApiResponse(responseCode = "404", description = "Movie crew member not found")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovieCrewMember(@PathVariable ObjectId id) {
        movieCrewMemberService.delete(id);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}

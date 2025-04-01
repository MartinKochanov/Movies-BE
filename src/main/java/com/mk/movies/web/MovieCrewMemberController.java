package com.mk.movies.web;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.mk.movies.domain.movie_crew_member.dto.MovieCrewMemberRequest;
import com.mk.movies.domain.movie_crew_member.dto.MovieCrewMemberUpdateRequest;
import com.mk.movies.domain.movie_crew_member.dto.MovieCrewMemberView;
import com.mk.movies.domain.movie_crew_member.service.MovieCrewMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/movie-crew-members")
public class MovieCrewMemberController {

    private final MovieCrewMemberService movieCrewMemberService;

    @PostMapping
    private ResponseEntity<MovieCrewMemberView> createMovieCrewMember(
        @ModelAttribute @Valid MovieCrewMemberRequest movieCrewMemberRequest) {
        return ResponseEntity
            .status(CREATED)
            .body(movieCrewMemberService.create(movieCrewMemberRequest));
    }

    @GetMapping
    private ResponseEntity<Page<MovieCrewMemberView>> getMovieCrewMember(Pageable pageable) {
        return ResponseEntity.ok().body(movieCrewMemberService.getAll(pageable));
    }

    @GetMapping("/{id}")
    private ResponseEntity<MovieCrewMemberView> getMovieCrewMemberById(@PathVariable String id) {
        return ResponseEntity.ok().body(movieCrewMemberService.getById(id));
    }

    @PatchMapping("/{id}")
    private ResponseEntity<MovieCrewMemberView> updateMovieCrewMember(@PathVariable String id,
        @ModelAttribute @Valid MovieCrewMemberUpdateRequest movieCrewMemberRequest) {
        return ResponseEntity.ok().body(movieCrewMemberService.update(id, movieCrewMemberRequest));
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteMovieCrewMember(@PathVariable String id) {
        movieCrewMemberService.delete(id);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}

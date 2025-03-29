package com.mk.movies.domain.movie.dto;

import com.mk.movies.domain.movie_crew_member.dto.MovieCrewMemberView;
import java.util.List;

public record MovieDetailsView(
    String id,
    String title,
    Integer duration,
    Integer releaseYear,
    List<String> genres,
    String plot,
    String imageUrl,
    String trailerUrl,
    String filmStudio,
    String basedOn,
    Boolean series,
    List<MovieCrewMemberView> cast,
    List<MovieCrewMemberView> directedBy,
    List<MovieCrewMemberView> producers,
    List<MovieCrewMemberView> writers
) {

}

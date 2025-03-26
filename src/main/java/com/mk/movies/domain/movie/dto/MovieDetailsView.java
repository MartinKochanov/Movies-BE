package com.mk.movies.domain.movie.dto;

import com.mk.movies.domain.movie_crew_member.document.MovieCrewMember;
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
    List<String> castIds,
    Boolean series,
    String directedById,
    String basedOn,
    String producerId,
    List<String> writersIds
) {

}

package com.mk.movies.domain.movie.dto;

import com.mk.movies.domain.movie.enums.Genre;
import java.util.List;

public record MovieUpdateRequest(
    String title,
    Integer duration,
    Integer releaseYear,
    List<Genre> genres,
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

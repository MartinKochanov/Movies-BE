package com.mk.movies.domain.movie.dto;

import com.mk.movies.domain.movie.enums.Genre;
import java.util.List;

public record MovieSimpleView(
    String id,
    String title,
    String tagline,
    Integer duration,
    Integer releaseYear,
    List<Genre> genres,
    Boolean series,
    String imageUrl,
    String trailerUrl
) {

}

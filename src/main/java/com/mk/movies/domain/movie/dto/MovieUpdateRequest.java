package com.mk.movies.domain.movie.dto;

import com.mk.movies.domain.movie.enums.Genre;
import java.util.List;
import org.bson.types.ObjectId;

public record MovieUpdateRequest(
    String title,
    Integer duration,
    Integer releaseYear,
    List<Genre> genres,
    String plot,
    String imageUrl,
    String trailerUrl,
    String filmStudio,
    String basedOn,
    Boolean series,
    List<ObjectId> castIds,
    List<ObjectId> directedByIds,
    List<ObjectId> producersIds,
    List<ObjectId> writersIds
) {

}

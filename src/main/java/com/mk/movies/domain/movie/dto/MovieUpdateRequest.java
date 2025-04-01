package com.mk.movies.domain.movie.dto;

import com.mk.movies.domain.movie.enums.Genre;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

public record MovieUpdateRequest(
    String title,

    @Min(value = 1, message = "Duration must be at least 1 minute")
    Integer duration,

    @Min(value = 1900, message = "Release year must be a valid year")
    Integer releaseYear,

    @Size(min = 1, message = "At least one genre must be provided")
    List<Genre> genres,

    String plot,

    MultipartFile imageUrl,

    MultipartFile trailerUrl,

    String filmStudio,

    String basedOn,

    Boolean series,

    @Size(min = 1, message = "At least one cast member must be provided")
    List<ObjectId> castIds,

    @Size(min = 1, message = "At least one director must be provided")
    List<ObjectId> directedByIds,

    @Size(min = 1, message = "At least one producer must be provided")
    List<ObjectId> producersIds,

    @Size(min = 1, message = "At least one writer must be provided")
    List<ObjectId> writersIds
) {

}
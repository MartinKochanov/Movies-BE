package com.mk.movies.domain.movie.dto;

import com.mk.movies.domain.movie.enums.Genre;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record MovieRequest(
    @NotBlank(message = "Title ìs required")
    String title,

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    Integer duration,

    @NotNull(message = "Release year is required")
    @Min(value = 1900, message = "Release year must be a valid year")
    Integer releaseYear,

    @NotNull(message = "Genre is required")
    @Size(min = 1, message = "At least one genre must be provided")
    List<Genre> genres,

    @NotBlank(message = "Plot is required")
    String plot,

    String imageUrl,
    String trailerUrl,

    @NotBlank(message = "Film studio is required")
    String filmStudio,

    @NotNull(message = "Cast is required")
    @Size(min = 1, message = "At least one cast member must be provided")
    List<String> castIds,

    Boolean series,

    @NotBlank(message = "Director is required")
    String directedById,

    String basedOn,

    @NotBlank(message = "Producer is required")
    String producerId,

    @NotNull(message = "Writer is required")
    @Size(min = 1, message = "At least one writer must be provided")
    List<String> writersIds
) {


}
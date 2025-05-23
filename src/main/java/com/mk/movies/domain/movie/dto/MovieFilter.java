package com.mk.movies.domain.movie.dto;

import com.mk.movies.domain.movie.enums.Genre;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieFilter {
    private String title;
    private List<Genre> genres;
    private Integer releaseYear;
    private Boolean isSeries;
}

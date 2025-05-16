package com.mk.movies.domain.movie.document;

import com.mk.movies.domain.movie.enums.Genre;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Movies")
public class Movie {

    @Id
    private ObjectId id;
    private String title;
    private int duration;
    private int releaseYear;
    private List<Genre> genres;
    private String plot;
    private String tagline;
    private String imageUrl;
    private String trailerUrl;
    private String filmStudio;
    private boolean series;
    private String basedOn;
    private List<ObjectId> castIds;
    private List<ObjectId> directedByIds;
    private List<ObjectId> producersIds;
    private List<ObjectId> writersIds;
}
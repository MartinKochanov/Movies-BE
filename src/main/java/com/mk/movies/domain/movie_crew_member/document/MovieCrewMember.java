package com.mk.movies.domain.movie_crew_member.document;

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
@Document(collection = "MovieCrewMembers")
public class MovieCrewMember {

    @Id
    private ObjectId id;
    private String firstName;
    private String lastName;
    private String imageUrl;
}

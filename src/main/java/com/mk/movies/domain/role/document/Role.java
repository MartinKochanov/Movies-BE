package com.mk.movies.domain.role.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Roles")
public class Role {

    @Id
    private ObjectId id;
    private String name;
    private ObjectId castId;
    private ObjectId movieId;
}

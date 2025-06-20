package com.mk.movies.domain.user.document;

import com.mk.movies.domain.user.enums.Role;
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
@Document(collection = "Users")
public class User {

    @Id
    private ObjectId id;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String imageUrl;
    private Role role;
}

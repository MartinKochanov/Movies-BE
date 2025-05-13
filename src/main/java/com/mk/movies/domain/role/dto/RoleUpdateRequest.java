package com.mk.movies.domain.role.dto;

import org.bson.types.ObjectId;

public record RoleUpdateRequest(
        String name,
        ObjectId castId
) {

}

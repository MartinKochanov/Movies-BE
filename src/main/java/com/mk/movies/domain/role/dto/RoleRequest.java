package com.mk.movies.domain.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;

public record RoleRequest(
    @NotBlank(message = "Role name is required")
    String name,

    @NotNull(message = "User ID is required")
    ObjectId castId,

    ObjectId movieId
) {

}

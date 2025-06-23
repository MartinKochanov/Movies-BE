package com.mk.movies.domain.user.dto;

import com.mk.movies.domain.user.enums.Role;

public record AuthenticatedUserView(
    String id,
    String email,
    String firstName,
    String lastName,
    String imageUrl,
    Role role
) {

}

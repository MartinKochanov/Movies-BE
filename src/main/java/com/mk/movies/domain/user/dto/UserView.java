package com.mk.movies.domain.user.dto;

public record UserView(
    String id,
    String firstName,
    String lastName,
    String email,
    String role
) {

}

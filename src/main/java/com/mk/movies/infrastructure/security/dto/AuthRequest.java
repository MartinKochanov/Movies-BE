package com.mk.movies.infrastructure.security.dto;

public record AuthRequest(
    String email,
    String password
) {

}

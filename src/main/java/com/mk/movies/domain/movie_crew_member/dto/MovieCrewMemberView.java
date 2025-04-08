package com.mk.movies.domain.movie_crew_member.dto;

public record MovieCrewMemberView(
    String id,
    String firstName,
    String lastName,
    String imageUrl,
    String role
) {

}

package com.mk.movies.domain.movie_crew_member.dto;

import com.mk.movies.domain.role.dto.RoleView;

public record MovieCrewMemberView(
    String id,
    String firstName,
    String lastName,
    String imageUrl,
    RoleView role
) {

}

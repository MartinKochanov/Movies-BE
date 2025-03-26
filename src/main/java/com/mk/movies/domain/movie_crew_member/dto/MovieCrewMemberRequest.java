package com.mk.movies.domain.movie_crew_member.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record MovieCrewMemberRequest(
    @NotBlank(message = "First name is required")
    @Length(min = 2, message = "First name must be at least 2 characters long")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Length(min = 2, message = "Last name must be at least 2 characters long")
    String lastName,

    String imageUrl
) {

}

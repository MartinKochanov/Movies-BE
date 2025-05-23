package com.mk.movies.domain.movie_crew_member.dto;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

public record MovieCrewMemberUpdateRequest(
    @Length(min = 2, message = "First name must be at least 2 characters long")
    String firstName,

    @Length(min = 2, message = "Last name must be at least 2 characters long")
    String lastName,

    MultipartFile image
) {

}

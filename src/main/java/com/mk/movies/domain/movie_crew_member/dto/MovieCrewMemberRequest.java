package com.mk.movies.domain.movie_crew_member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

public record MovieCrewMemberRequest(
    @NotBlank(message = "First name is required")
    @Length(min = 2, message = "First name must be at least 2 characters long")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Length(min = 2, message = "Last name must be at least 2 characters long")
    String lastName,

    @NotNull(message = "Image is required")
    MultipartFile image
) {

}

package com.mk.movies.domain.user.dto;

import org.springframework.web.multipart.MultipartFile;

public record UserUpdateRequest(
    String firstName,
    String lastName,
    MultipartFile image
) {

}

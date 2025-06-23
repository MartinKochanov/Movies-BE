package com.mk.movies.domain.user.dto;

import com.mk.movies.domain.user.enums.Role;
import com.mk.movies.domain.user.validation.SuperAdminFieldOnly;
import com.mk.movies.domain.user.validation.ValidRole;
import org.springframework.web.multipart.MultipartFile;

public record UserUpdateRequest(
    String firstName,
    String lastName,
    MultipartFile image,
    @SuperAdminFieldOnly
        @ValidRole
    Role role
) {

}

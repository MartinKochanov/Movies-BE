package com.mk.movies.domain.user.dto;

import com.mk.movies.domain.user.validation.UniqueEmail;
import com.mk.movies.domain.user.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(

    @NotBlank(message = "First name is required")
    String firstName,

    @NotBlank(message = "Last name is required")
    String lastName,

    @UniqueEmail
    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    String email,

    @ValidPassword
    @NotBlank(message = "Password is required")
    String password
) {

}

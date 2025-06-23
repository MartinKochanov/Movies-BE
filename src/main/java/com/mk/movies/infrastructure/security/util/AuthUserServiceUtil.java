package com.mk.movies.infrastructure.security.util;

import com.mk.movies.infrastructure.security.dto.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUserServiceUtil {

    public static CustomUserDetails extractAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) authentication.getPrincipal();
    }
}

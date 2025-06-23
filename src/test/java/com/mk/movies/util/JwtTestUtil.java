package com.mk.movies.util;

import com.mk.movies.domain.user.document.User;
import com.mk.movies.infrastructure.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTestUtil {

    private final JwtService jwtService;

    public String generateToken(User user) {
        return "Bearer " + jwtService.generateToken(user.getEmail());
    }
}

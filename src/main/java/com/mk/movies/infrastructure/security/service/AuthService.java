package com.mk.movies.infrastructure.security.service;

import static com.mk.movies.infrastructure.security.util.AuthUserServiceUtil.extractAuthenticatedUser;

import com.mk.movies.domain.user.dto.AuthenticatedUserView;
import com.mk.movies.infrastructure.mappers.UserMapper;
import com.mk.movies.infrastructure.security.dto.AuthRequest;
import com.mk.movies.infrastructure.security.dto.AuthResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
            ));
        String token = jwtService.generateToken(request.email());
        return new AuthResponse(token);
    }

    public AuthenticatedUserView getAuthenticatedUser() {
        var user = extractAuthenticatedUser();
        return userMapper.toAuthenticatedUserView(user);
    }
}

package com.mk.movies.infrastructure.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mk.movies.domain.user.dto.AuthenticatedUserView;
import com.mk.movies.infrastructure.mappers.UserMapper;
import com.mk.movies.infrastructure.security.dto.AuthRequest;
import com.mk.movies.infrastructure.security.dto.CustomUserDetails;
import com.mk.movies.infrastructure.security.util.AuthUserServiceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private UserMapper userMapper;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        authenticationManager = mock(AuthenticationManager.class);
        userMapper = mock(UserMapper.class);
        authService = new AuthService(jwtService, authenticationManager, userMapper);
    }

    @Test
    void authenticate_returnsAuthResponse_givenValidRequest() {
        var request = new AuthRequest("user@example.com", "password");
        String token = "jwt-token";
        when(jwtService.generateToken(request.email())).thenReturn(token);

        var response = authService.authenticate(request);

        assertEquals(token, response.token());
        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
            ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());
        UsernamePasswordAuthenticationToken authToken = captor.getValue();
        assertEquals(request.email(), authToken.getPrincipal());
        assertEquals(request.password(), authToken.getCredentials());
    }

    @Test
    void getAuthenticatedUser_returnsAuthenticatedUserView() {
        var user = mock(CustomUserDetails.class);
        var view = mock(AuthenticatedUserView.class);

        try (var mocked = mockStatic(
            AuthUserServiceUtil.class)) {
            mocked.when(
                    AuthUserServiceUtil::extractAuthenticatedUser)
                .thenReturn(user);
            when(userMapper.toAuthenticatedUserView(user)).thenReturn(view);

            var result = authService.getAuthenticatedUser();

            assertEquals(view, result);
            verify(userMapper).toAuthenticatedUserView(user);
        }
    }
}
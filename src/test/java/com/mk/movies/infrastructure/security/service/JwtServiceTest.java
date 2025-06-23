package com.mk.movies.infrastructure.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();
        Field secretField = JwtService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtService, "ZmFrZXNlY3JldGZha2VzZWNyZXRmYWtlc2VjcmV0ZmFrZXNlY3JldA==");

        Field expField = JwtService.class.getDeclaredField("expirationTime");
        expField.setAccessible(true);
        expField.set(jwtService, 1000 * 60 * 60L); // 1 hour
    }

    @Test
    void generateToken_and_extractEmail_shouldWork() {
        String email = "user@example.com";
        String token = jwtService.generateToken(email);

        assertNotNull(token);
        assertEquals(email, jwtService.extractEmail(token));
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        String email = "user@example.com";
        String token = jwtService.generateToken(email);
        var userDetails = User.withUsername(email).password("pass").roles("USER").build();

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenExpired_shouldReturnFalseForFreshToken() {
        String email = "user@example.com";
        String token = jwtService.generateToken(email);

        assertFalse(jwtService.isTokenExpired(token));
    }
}
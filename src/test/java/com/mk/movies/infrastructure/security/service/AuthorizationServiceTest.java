package com.mk.movies.infrastructure.security.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import com.mk.movies.infrastructure.security.util.AuthUserServiceUtil;
import com.mk.movies.infrastructure.security.dto.CustomUserDetails;
import com.mk.movies.domain.user.document.User;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Test
    void isAccountOwner_returnsTrue_whenUserIdMatchesAuthenticatedUser() {
        var userId = new ObjectId();
        var user = new User();
        user.setId(userId);
        var customUserDetails = new CustomUserDetails(user);

        try (var mocked = mockStatic(AuthUserServiceUtil.class)) {
            mocked.when(AuthUserServiceUtil::extractAuthenticatedUser)
                  .thenReturn(customUserDetails);

            var service = new AuthorizationService();
            assertTrue(service.isAccountOwner(userId));
        }
    }

    @Test
    void isAccountOwner_returnsFalse_whenUserIdDoesNotMatchAuthenticatedUser() {
        var userId = new ObjectId();
        var anotherId = new ObjectId();
        var user = new User();
        user.setId(anotherId);
        var customUserDetails = new CustomUserDetails(user);

        try (var mocked = mockStatic(AuthUserServiceUtil.class)) {
            mocked.when(AuthUserServiceUtil::extractAuthenticatedUser)
                  .thenReturn(customUserDetails);

            var service = new AuthorizationService();
            assertFalse(service.isAccountOwner(userId));
        }
    }
}
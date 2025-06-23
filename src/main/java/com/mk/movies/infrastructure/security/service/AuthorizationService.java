package com.mk.movies.infrastructure.security.service;

import com.mk.movies.infrastructure.security.util.AuthUserServiceUtil;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component(value = "authorizationService")
public class AuthorizationService {
    public boolean isAccountOwner(ObjectId userId) {
        var authenticatedUser = AuthUserServiceUtil.extractAuthenticatedUser();
        return authenticatedUser.getId().equals(userId);
    }
}

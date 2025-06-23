package com.mk.movies.domain.user.validation;

import com.mk.movies.domain.user.enums.Role;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidRoleValidator implements ConstraintValidator<ValidRole, Role> {

    @Override
    public boolean isValid(Role role, ConstraintValidatorContext context) {
        if (role == null) {
            return true;
        }

        return Role.CLIENT == role || Role.ADMIN == role;
    }
}

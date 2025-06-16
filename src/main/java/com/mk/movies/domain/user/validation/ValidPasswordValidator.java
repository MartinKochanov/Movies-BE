package com.mk.movies.domain.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;

public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {

        if (password != null) {
            var validator = new PasswordValidator(
                Arrays.asList(
                    new LengthRule(8, 20),
                    new CharacterRule(EnglishCharacterData.LowerCase, 1),
                    new CharacterRule(EnglishCharacterData.UpperCase, 1),
                    new CharacterRule(EnglishCharacterData.Digit, 1),
                    new CharacterRule(EnglishCharacterData.Special, 1),
                    new WhitespaceRule()
                )
            );

            RuleResult result = validator.validate(new PasswordData(password));

            if (result.isValid()) {
                return true;
            }

            context.buildConstraintViolationWithTemplate(
                    validator.getMessages(result)
                        .stream().findFirst().get())
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        }

        return false;
    }
}

package com.vidasoft.diary.validation;

import com.vidasoft.diary.model.Person;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueIdentityValidator implements ConstraintValidator<UniqueIdentity, String> {

    @Override
    public boolean isValid(String identity, ConstraintValidatorContext constraintValidatorContext) {
        return Person.find("identity", identity).firstResultOptional().isEmpty();
    }
}

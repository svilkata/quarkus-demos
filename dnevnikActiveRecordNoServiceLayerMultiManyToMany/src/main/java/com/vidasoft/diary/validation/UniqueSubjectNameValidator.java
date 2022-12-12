package com.vidasoft.diary.validation;

import com.vidasoft.diary.model.Subject;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Locale;

public class UniqueSubjectNameValidator implements ConstraintValidator<UniqueSubjectName, String> {
    @Override
    public boolean isValid(String name, ConstraintValidatorContext constraintValidatorContext) {
        return Subject.find("name", name.toLowerCase(Locale.ROOT)).firstResultOptional().isEmpty();
    }
}

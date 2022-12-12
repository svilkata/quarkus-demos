package com.vidasoft.diary.validation;

import com.vidasoft.diary.clazz.ClazzDTO;
import com.vidasoft.diary.model.Clazz;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueClazzNameValidator implements ConstraintValidator<UniqueClazzName, ClazzDTO> {
    private String first;
    private String second;
    private String message;

    @Override
    public void initialize(UniqueClazzName constraintAnnotation) {
        this.first = constraintAnnotation.firstField();
        this.second = constraintAnnotation.secondField();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(ClazzDTO ClazzDTOObject, ConstraintValidatorContext context) {
        String clazzNumber = ClazzDTOObject.clazzNumber;
        String clazzLetter = ClazzDTOObject.subclazzInitial;
        return Clazz.find("clazzNumber = ?1 AND subclazzInitial = ?2", clazzNumber, clazzLetter).firstResultOptional().isEmpty();
    }
}

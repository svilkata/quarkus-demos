package com.vidasoft.diary.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {UniqueClazzNameValidator.class})
@Documented
public @interface UniqueClazzName {
    String firstField();
    String secondField();

    String message() default "The clazzName you have entered already exists in the database/is not unique";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

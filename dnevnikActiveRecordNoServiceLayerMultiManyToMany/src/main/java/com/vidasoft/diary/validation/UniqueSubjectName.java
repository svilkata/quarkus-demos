package com.vidasoft.diary.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {UniqueSubjectNameValidator.class})
@Documented
public @interface UniqueSubjectName {

    String message() default "The subjectName you have entered already exists in the database/is not unique";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

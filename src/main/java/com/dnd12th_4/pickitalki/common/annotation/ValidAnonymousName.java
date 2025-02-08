package com.dnd12th_4.pickitalki.common.annotation;

import com.dnd12th_4.pickitalki.common.validator.AnonymousNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AnonymousNameValidator.class)
public @interface ValidAnonymousName {
    String message() default "If isAnonymous is true, anonymousName must not be blank.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
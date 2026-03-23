package com.example.baitap.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = DeadlineAfterTodayValidator.class)
public @interface DeadlineAfterToday {
    String message() default "deadline must be after current date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}


package com.example.baitap.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DeadlineAfterTodayValidator implements ConstraintValidator<DeadlineAfterToday, LocalDate> {
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // để @NotNull xử lý riêng
        }
        return value.isAfter(LocalDate.now());
    }
}


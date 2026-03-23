package com.example.baitap.validation;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DeadlineAfterTodayValidatorTest {

    @Test
    void isValid_shouldReturnTrue_whenDeadlineIsAfterToday() {
        DeadlineAfterTodayValidator validator = new DeadlineAfterTodayValidator();
        assertTrue(validator.isValid(LocalDate.now().plusDays(1), null));
    }

    @Test
    void isValid_shouldReturnFalse_whenDeadlineIsToday() {
        DeadlineAfterTodayValidator validator = new DeadlineAfterTodayValidator();
        assertFalse(validator.isValid(LocalDate.now(), null));
    }

    @Test
    void isValid_shouldReturnFalse_whenDeadlineIsBeforeToday() {
        DeadlineAfterTodayValidator validator = new DeadlineAfterTodayValidator();
        assertFalse(validator.isValid(LocalDate.now().minusDays(1), null));
    }
}


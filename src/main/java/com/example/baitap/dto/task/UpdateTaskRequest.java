package com.example.baitap.dto.task;

import com.example.baitap.validation.DeadlineAfterToday;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class UpdateTaskRequest {
    @NotBlank(message = "title is required")
    @Size(min = 3, max = 200, message = "title length must be 3..200")
    private String title;

    @Size(max = 1000, message = "description max length is 1000")
    private String description;

    @NotNull(message = "deadline is required")
    @DeadlineAfterToday
    private LocalDate deadline;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
}


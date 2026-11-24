package dev.portfolio.taskflow.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;

public final class TaskDtos {
    private TaskDtos() {
    }

    public record CreateRequest(
            @NotBlank @Size(max = 150) String title,
            @Size(max = 2000) String description,
            LocalDate dueDate) {
    }

    public record UpdateRequest(
            @NotBlank @Size(max = 150) String title,
            @Size(max = 2000) String description,
            @NotNull TaskStatus status,
            LocalDate dueDate) {
    }

    public record Response(
            Long id,
            String title,
            String description,
            TaskStatus status,
            LocalDate dueDate,
            Instant createdAt,
            Instant updatedAt) {
        static Response from(Task task) {
            return new Response(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(),
                    task.getDueDate(), task.getCreatedAt(), task.getUpdatedAt());
        }
    }
}

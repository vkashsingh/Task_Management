package com.example.taskmanager.dto;

import com.example.taskmanager.entity.TaskPriority;
import com.example.taskmanager.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateTaskRequest {

    @Schema(description = "Title of the task", example = "Learn Kubernetes", maxLength = 100)
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @Schema(description = "Detailed description of the task", example = "Practice Kubernetes deployment", maxLength = 500)
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Schema(description = "Current status of the task", example = "TODO")
    @NotNull(message = "Status is required")
    private TaskStatus status;

    @Schema(description = "Priority level of the task", example = "HIGH")
    @NotNull(message = "Priority is required")
    private TaskPriority priority;

    public CreateTaskRequest() {
    }

    public CreateTaskRequest(String title, String description, TaskStatus status, TaskPriority priority) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
    }

    public static CreateTaskRequestBuilder builder() {
        return new CreateTaskRequestBuilder();
    }

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

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public static class CreateTaskRequestBuilder {
        private String title;
        private String description;
        private TaskStatus status;
        private TaskPriority priority;

        CreateTaskRequestBuilder() {
        }

        public CreateTaskRequestBuilder title(String title) {
            this.title = title;
            return this;
        }

        public CreateTaskRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public CreateTaskRequestBuilder status(TaskStatus status) {
            this.status = status;
            return this;
        }

        public CreateTaskRequestBuilder priority(TaskPriority priority) {
            this.priority = priority;
            return this;
        }

        public CreateTaskRequest build() {
            return new CreateTaskRequest(title, description, status, priority);
        }
    }
}

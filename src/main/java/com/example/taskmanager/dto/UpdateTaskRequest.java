package com.example.taskmanager.dto;

import com.example.taskmanager.entity.TaskPriority;
import com.example.taskmanager.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateTaskRequest {

    @Schema(description = "Updated title of the task", example = "Master Kubernetes & Helm", maxLength = 100)
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @Schema(description = "Updated description of the task", example = "Practice Helm charts and Argo CD", maxLength = 500)
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Schema(description = "Updated status of the task", example = "IN_PROGRESS")
    @NotNull(message = "Status is required")
    private TaskStatus status;

    @Schema(description = "Updated priority level of the task", example = "HIGH")
    @NotNull(message = "Priority is required")
    private TaskPriority priority;

    public UpdateTaskRequest() {
    }

    public UpdateTaskRequest(String title, String description, TaskStatus status, TaskPriority priority) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
    }

    public static UpdateTaskRequestBuilder builder() {
        return new UpdateTaskRequestBuilder();
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

    public static class UpdateTaskRequestBuilder {
        private String title;
        private String description;
        private TaskStatus status;
        private TaskPriority priority;

        UpdateTaskRequestBuilder() {
        }

        public UpdateTaskRequestBuilder title(String title) {
            this.title = title;
            return this;
        }

        public UpdateTaskRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public UpdateTaskRequestBuilder status(TaskStatus status) {
            this.status = status;
            return this;
        }

        public UpdateTaskRequestBuilder priority(TaskPriority priority) {
            this.priority = priority;
            return this;
        }

        public UpdateTaskRequest build() {
            return new UpdateTaskRequest(title, description, status, priority);
        }
    }
}

package com.example.taskmanager.service;

import com.example.taskmanager.dto.CreateTaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.dto.UpdateTaskRequest;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskPriority;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = Task.builder()
                .id(1L)
                .title("Learn Kubernetes")
                .description("Practice Kubernetes deployment")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.HIGH)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should successfully create a task")
    void createTask_Success() {
        CreateTaskRequest request = CreateTaskRequest.builder()
                .title("Learn Kubernetes")
                .description("Practice Kubernetes deployment")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.HIGH)
                .build();

        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskResponse response = taskService.createTask(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Learn Kubernetes");
        assertThat(response.getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(response.getPriority()).isEqualTo(TaskPriority.HIGH);

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should return all tasks")
    void getAllTasks_Success() {
        Task secondTask = Task.builder()
                .id(2L)
                .title("Setup CI/CD")
                .description("Build Jenkins pipeline")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.MEDIUM)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(taskRepository.findAll()).thenReturn(List.of(sampleTask, secondTask));

        List<TaskResponse> responses = taskService.getAllTasks();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getTitle()).isEqualTo("Learn Kubernetes");
        assertThat(responses.get(1).getTitle()).isEqualTo("Setup CI/CD");

        verify(taskRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return task by ID when task exists")
    void getTaskById_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        TaskResponse response = taskService.getTaskById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Learn Kubernetes");

        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException when getting non-existent task")
    void getTaskById_NotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(99L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task with id 99 was not found");

        verify(taskRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Should successfully update task when task exists")
    void updateTask_Success() {
        UpdateTaskRequest updateRequest = UpdateTaskRequest.builder()
                .title("Master Kubernetes")
                .description("Practice Helm and Argo CD")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.HIGH)
                .build();

        Task updatedTask = Task.builder()
                .id(1L)
                .title("Master Kubernetes")
                .description("Practice Helm and Argo CD")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.HIGH)
                .createdAt(sampleTask.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        TaskResponse response = taskService.updateTask(1L, updateRequest);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Master Kubernetes");
        assertThat(response.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);

        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException when updating non-existent task")
    void updateTask_NotFound() {
        UpdateTaskRequest updateRequest = UpdateTaskRequest.builder()
                .title("Master Kubernetes")
                .description("Practice Helm")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.HIGH)
                .build();

        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(99L, updateRequest))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task with id 99 was not found");

        verify(taskRepository, times(1)).findById(99L);
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully delete task when task exists")
    void deleteTask_Success() {
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException when deleting non-existent task")
    void deleteTask_NotFound() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> taskService.deleteTask(99L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task with id 99 was not found");

        verify(taskRepository, times(1)).existsById(99L);
        verify(taskRepository, never()).deleteById(any());
    }
}

package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskPriority;
import com.example.taskmanager.entity.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    @DisplayName("Should save and retrieve task successfully")
    void saveAndFindTask_Success() {
        Task task = Task.builder()
                .title("Repository Test Task")
                .description("Testing JPA persistence")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.LOW)
                .build();

        Task savedTask = taskRepository.save(task);

        assertThat(savedTask.getId()).isNotNull();
        assertThat(savedTask.getCreatedAt()).isNotNull();
        assertThat(savedTask.getUpdatedAt()).isNotNull();

        Optional<Task> foundTask = taskRepository.findById(savedTask.getId());
        assertThat(foundTask).isPresent();
        assertThat(foundTask.get().getTitle()).isEqualTo("Repository Test Task");
    }

    @Test
    @DisplayName("Should delete task successfully")
    void deleteTask_Success() {
        Task task = Task.builder()
                .title("Task to Delete")
                .description("Will be deleted")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.LOW)
                .build();

        Task savedTask = taskRepository.save(task);
        Long id = savedTask.getId();

        taskRepository.deleteById(id);

        Optional<Task> foundTask = taskRepository.findById(id);
        assertThat(foundTask).isEmpty();
    }
}

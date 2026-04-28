package com.example.k8sdemo.service;

import com.example.k8sdemo.exception.TaskNotFoundException;
import com.example.k8sdemo.model.Task;
import com.example.k8sdemo.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock  TaskRepository repository;
    @InjectMocks TaskService service;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setTitle("Write tests");
        task.setDescription("Always write tests");
    }

    @Test
    @DisplayName("create() saves and returns the task")
    void create_savesTask() {
        when(repository.save(any(Task.class))).thenReturn(task);
        Task result = service.create(task);
        assertThat(result.getTitle()).isEqualTo("Write tests");
        verify(repository).save(task);
    }

    @Test
    @DisplayName("findAll() returns all tasks from repository")
    void findAll_returnsList() {
        when(repository.findAll()).thenReturn(List.of(task));
        List<Task> results = service.findAll();
        assertThat(results).hasSize(1);
        verify(repository).findAll();
    }

    @Test
    @DisplayName("findById() returns task when found")
    void findById_returnsTask() {
        when(repository.findById(1L)).thenReturn(Optional.of(task));
        Task result = service.findById(1L);
        assertThat(result.getTitle()).isEqualTo("Write tests");
    }

    @Test
    @DisplayName("findById() throws when task is missing")
    void findById_throwsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("update() changes fields and saves")
    void update_changesFields() {
        Task updated = new Task();
        updated.setTitle("Updated title");
        updated.setDescription("Updated desc");
        updated.setCompleted(true);

        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(any(Task.class))).thenReturn(task);

        Task result = service.update(1L, updated);

        assertThat(result.getTitle()).isEqualTo("Updated title");
        verify(repository).save(task);
    }

    @Test
    @DisplayName("update() throws when task does not exist")
    void update_throwsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        Task updated = new Task();
        updated.setTitle("x");
        assertThatThrownBy(() -> service.update(99L, updated))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    @DisplayName("delete() calls deleteById on existing task")
    void delete_callsRepository() {
        when(repository.findById(1L)).thenReturn(Optional.of(task));
        service.delete(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("delete() throws when task does not exist")
    void delete_throwsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(TaskNotFoundException.class);
    }
}

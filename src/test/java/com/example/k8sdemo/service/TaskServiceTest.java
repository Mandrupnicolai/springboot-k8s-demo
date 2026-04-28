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
    @DisplayName("findById() throws when task is missing")
    void findById_throwsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("delete() calls deleteById on existing task")
    void delete_callsRepository() {
        when(repository.findById(1L)).thenReturn(Optional.of(task));
        service.delete(1L);
        verify(repository).deleteById(1L);
    }
}

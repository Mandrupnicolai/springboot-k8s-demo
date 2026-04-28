package com.example.k8sdemo.controller;

import com.example.k8sdemo.model.Task;
import com.example.k8sdemo.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired TaskRepository repository;
    @Autowired ObjectMapper mapper;

    @BeforeEach
    void clean() { repository.deleteAll(); }

    @Test
    @DisplayName("POST /api/v1/tasks creates a task and returns 201")
    void createTask_returns201() throws Exception {
        Task t = new Task();
        t.setTitle("My first task");
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(t)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.title").value("My first task"));
    }

    @Test
    @DisplayName("GET /api/v1/tasks/{id} returns 404 for unknown id")
    void getTask_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/999"))
               .andExpect(status().isNotFound());
    }
}

package com.example.k8sdemo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaskTest {

    @Test
    @DisplayName("setters and getters round-trip correctly")
    void gettersAndSetters() {
        Task task = new Task();
        task.setTitle("Test title");
        task.setDescription("Test description");
        task.setCompleted(true);

        assertThat(task.getTitle()).isEqualTo("Test title");
        assertThat(task.getDescription()).isEqualTo("Test description");
        assertThat(task.isCompleted()).isTrue();
    }

    @Test
    @DisplayName("onCreate() sets createdAt and updatedAt")
    void onCreate_setsTimestamps() {
        Task task = new Task();
        task.onCreate();

        assertThat(task.getCreatedAt()).isNotNull();
        assertThat(task.getUpdatedAt()).isNotNull();
        assertThat(task.getCreatedAt()).isEqualTo(task.getUpdatedAt());
    }

    @Test
    @DisplayName("onUpdate() updates updatedAt without changing createdAt")
    void onUpdate_changesUpdatedAt() throws InterruptedException {
        Task task = new Task();
        task.onCreate();
        Thread.sleep(5);
        task.onUpdate();

        assertThat(task.getUpdatedAt()).isAfter(task.getCreatedAt());
    }
}

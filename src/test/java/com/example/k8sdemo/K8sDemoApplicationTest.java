package com.example.k8sdemo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class K8sDemoApplicationTest {

    @Test
    @DisplayName("Spring context loads successfully")
    void contextLoads() {
        // Verifies the entire application context starts without errors
    }
}

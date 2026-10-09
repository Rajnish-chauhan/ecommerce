package com.project.ecommerce;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // Loads configurations from application-test.properties
class EcommerceApplicationTests {

    @Test
    @DisplayName("Context Load Test - Verifies that the Spring application context starts without errors")
    void contextLoads() {
        // This test passes if all Spring Boot beans and configurations initialize successfully
    }
}
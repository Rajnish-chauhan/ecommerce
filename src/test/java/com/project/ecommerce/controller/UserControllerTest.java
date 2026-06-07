package com.project.ecommerce.controller;

import com.project.ecommerce.model.User;
import com.project.ecommerce.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 1. Completely disable auto-configured security features for this controller slice
@WebMvcTest(controllers = UserController.class, useDefaultFilters = false)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    // 2. Mock your custom SecurityConfig bean so Spring replaces the crashing bean with a harmless stub
    @MockitoBean
    private com.project.ecommerce.config.SecurityConfig securityConfig;

    @Test
    void testLoginUser_Success() throws Exception {
        // Arrange
        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setName("Test User");

        when(userService.loginUser("test@example.com", "pass")).thenReturn(mockUser);

        // Act & Assert
        String json = "{\"email\":\"test@example.com\", \"password\":\"pass\"}";

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }
}

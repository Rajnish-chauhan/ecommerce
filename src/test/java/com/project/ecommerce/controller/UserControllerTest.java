package com.project.ecommerce.controller;

import com.project.ecommerce.dto.UserResponseDTO;
import com.project.ecommerce.dto.UserRequestDTO;
import com.project.ecommerce.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        // Initialize standalone MockMvc without requiring Spring ApplicationContext or @MockBean
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @DisplayName("GET /users/check-email - Should return availability status")
    void testCheckEmailExists() throws Exception {
        when(userService.isEmailRegistered("client@example.com")).thenReturn(true);

        mockMvc.perform(get("/users/check-email")
                        .param("email", "client@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));
    }

    @Test
    @DisplayName("POST /users/register - Should register user and return profile")
    void testRegisterUser() throws Exception {
        UserResponseDTO mockResponse = new UserResponseDTO(
                "user_001", "Rajnish", "rajnish@example.com", "+919876543210", "USER", null, null, null
        );

        when(userService.registerNewUser(any(UserRequestDTO.class))).thenReturn(mockResponse);

        String jsonPayload = """
            {
                "name": "Rajnish",
                "email": "rajnish@example.com",
                "password": "Password123",
                "phoneNumber": "+919876543210"
            }
            """;

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user_001"))
                .andExpect(jsonPath("$.name").value("Rajnish"));
    }

    @Test
    @DisplayName("POST /users/login - Should authenticate with valid credentials")
    void testLoginUser() throws Exception {
        UserResponseDTO mockResponse = new UserResponseDTO(
                "user_001", "Rajnish", "rajnish@example.com", "+919876543210", "USER", null, null, null
        );

        when(userService.loginUser("rajnish@example.com", "Password123")).thenReturn(mockResponse);

        String jsonPayload = """
            {
                "email": "rajnish@example.com",
                "password": "Password123"
            }
            """;

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("rajnish@example.com"));
    }

    @Test
    @DisplayName("DELETE /users/delete/{id} - Should return success confirmation")
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/delete/{id}", "user_001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account successfully deleted."));
    }
}
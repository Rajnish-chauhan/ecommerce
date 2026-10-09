package com.project.ecommerce.service;

import com.project.ecommerce.dto.UserProfileUpdateDTO;
import com.project.ecommerce.dto.UserRequestDTO;
import com.project.ecommerce.dto.UserResponseDTO;
import com.project.ecommerce.model.User;
import com.project.ecommerce.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User sampleUser;
    private UserRequestDTO userRequestDTO;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId("user_123");
        sampleUser.setName("John Doe");
        sampleUser.setEmail("john@example.com");
        sampleUser.setPhoneNumber("+919876543210");
        sampleUser.setPassword("encodedPassword123");
        sampleUser.setRole("USER");
        sampleUser.setVerified(true);

        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("John Doe");
        userRequestDTO.setEmail("john@example.com");
        userRequestDTO.setPassword("plainPassword123");
        userRequestDTO.setPhoneNumber("+919876543210");
        userRequestDTO.setAddress("Delhi, India");
        userRequestDTO.setDob("2000-01-01");
    }

    @Test
    @DisplayName("Should successfully register a new user")
    void testRegisterNewUser_Success() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        // Act
        UserResponseDTO response = userService.registerNewUser(userRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("john@example.com", response.getEmail());
        assertEquals("John Doe", response.getName());
        verify(emailService, times(1)).sendRegistrationEmail(anyString(), anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when registering with an existing email")
    void testRegisterNewUser_EmailAlreadyExists() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(sampleUser);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerNewUser(userRequestDTO);
        });

        assertTrue(exception.getMessage().contains("already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should successfully authenticate user with valid credentials")
    void testLoginUser_Success() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(sampleUser);
        when(passwordEncoder.matches("plainPassword123", "encodedPassword123")).thenReturn(true);

        // Act
        UserResponseDTO response = userService.loginUser("john@example.com", "plainPassword123");

        // Assert
        assertNotNull(response);
        assertEquals("user_123", response.getId());
    }

    @Test
    @DisplayName("Should throw exception when password verification fails")
    void testLoginUser_InvalidPassword() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(sampleUser);
        when(passwordEncoder.matches("wrongPassword", "encodedPassword123")).thenReturn(false);

        // Act & Assert
        assertThrows(SecurityException.class, () -> {
            userService.loginUser("john@example.com", "wrongPassword");
        });
    }

    @Test
    @DisplayName("Should update user profile and change password when current password matches")
    void testUpdateUserProfile_WithPasswordChange() {
        // Arrange
        UserProfileUpdateDTO updateDTO = new UserProfileUpdateDTO();
        updateDTO.setName("John Updated");
        updateDTO.setCurrentPassword("encodedPassword123");
        updateDTO.setNewPassword("newSecret456");

        when(userRepository.findById("user_123")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("encodedPassword123", sampleUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newSecret456")).thenReturn("hashedNewSecret456");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        // Act
        UserResponseDTO result = userService.updateUserProfile("user_123", updateDTO);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder, times(1)).encode("newSecret456");
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    @DisplayName("Should permanently delete user account")
    void testDeleteUserAccount_Success() {
        // Arrange
        when(userRepository.existsById("user_123")).thenReturn(true);

        // Act
        userService.deleteUserAccount("user_123");

        // Assert
        verify(userRepository, times(1)).deleteById("user_123");
    }
}
package com.project.ecommerce.service;

import com.project.ecommerce.dto.UserProfileUpdateDTO;
import com.project.ecommerce.dto.UserRequestDTO;
import com.project.ecommerce.dto.UserResponseDTO;
import com.project.ecommerce.exception.ResourceNotFoundException;
import com.project.ecommerce.model.User;
import com.project.ecommerce.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Check if an email is already associated with an account
    public boolean isEmailRegistered(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return userRepository.findByEmail(email.trim().toLowerCase()) != null;
    }

    public UserResponseDTO registerNewUser(UserRequestDTO requestDto) {
        String normalizedEmail = requestDto.getEmail().trim().toLowerCase();
        if (userRepository.findByEmail(normalizedEmail) != null) {
            throw new IllegalArgumentException("An account with this email address already exists. Please sign in instead.");
        }

        User user = new User();
        user.setName(requestDto.getName().trim());
        user.setEmail(normalizedEmail);
        user.setPhoneNumber(requestDto.getPhoneNumber() != null ? requestDto.getPhoneNumber().trim() : null);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setDob(requestDto.getDob());
        user.setAddress(requestDto.getAddress() != null ? requestDto.getAddress().trim() : null);
        user.setRole(normalizedEmail.equalsIgnoreCase("admin@example.com") ? "ADMIN" : "USER");
        user.setVerified(true);

        User savedUser = userRepository.save(user);

        try {
            emailService.sendRegistrationEmail(savedUser.getEmail(), savedUser.getName());
        } catch (Exception e) {
            System.err.println("Registration welcome email notification failed: " + e.getMessage());
        }

        return convertToResponseDTO(savedUser);
    }

    public UserResponseDTO loginUser(String email, String password) {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Email and password are required.");
        }
        User existingUser = userRepository.findByEmail(email.trim().toLowerCase());
        if (existingUser == null) {
            throw new ResourceNotFoundException("Account not found. Please create an account first.");
        }
        if (!passwordEncoder.matches(password, existingUser.getPassword())) {
            throw new SecurityException("Invalid credentials. Please verify your password.");
        }
        return convertToResponseDTO(existingUser);
    }

    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email.trim().toLowerCase());
        if (user == null) {
            throw new ResourceNotFoundException("User not found with email: " + email);
        }
        return convertToResponseDTO(user);
    }

    public UserResponseDTO updateUserProfile(String id, UserProfileUpdateDTO updateDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User account not found"));

        if (updateDto.getName() != null && !updateDto.getName().trim().isEmpty()) {
            existingUser.setName(updateDto.getName().trim());
        }
        if (updateDto.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(updateDto.getPhoneNumber().trim());
        }
        if (updateDto.getDob() != null) {
            existingUser.setDob(updateDto.getDob());
        }
        if (updateDto.getAddress() != null) {
            existingUser.setAddress(updateDto.getAddress().trim());
        }
        if (updateDto.getProfileImageUrl() != null && !updateDto.getProfileImageUrl().trim().isEmpty()) {
            existingUser.setProfileImageUrl(updateDto.getProfileImageUrl().trim());
        }

        // Handle secure password modification
        if (updateDto.getNewPassword() != null && !updateDto.getNewPassword().trim().isEmpty()) {
            if (updateDto.getCurrentPassword() == null || updateDto.getCurrentPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("Current password is required to set a new password.");
            }
            if (!passwordEncoder.matches(updateDto.getCurrentPassword(), existingUser.getPassword())) {
                throw new SecurityException("The current password entered is incorrect.");
            }
            if (updateDto.getNewPassword().length() < 6) {
                throw new IllegalArgumentException("New password must contain at least 6 characters.");
            }
            existingUser.setPassword(passwordEncoder.encode(updateDto.getNewPassword().trim()));
        }

        User savedUser = userRepository.save(existingUser);
        return convertToResponseDTO(savedUser);
    }

    // Permanently remove a user account from MongoDB
    public void deleteUserAccount(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User account not found for deletion.");
        }
        userRepository.deleteById(id);
    }

    public void processOAuthPostLogin(String email, String name, String picture) {
        String cleanEmail = email.trim().toLowerCase();
        if (userRepository.findByEmail(cleanEmail) == null) {
            User newUser = new User();
            newUser.setEmail(cleanEmail);
            newUser.setName(name);
            newUser.setProfileImageUrl(picture);
            newUser.setRole("USER");
            newUser.setVerified(true);
            userRepository.save(newUser);
            try {
                emailService.sendRegistrationEmail(cleanEmail, name);
            } catch (Exception e) {
                System.err.println("Google welcome email notification failed: " + e.getMessage());
            }
        }
    }

    private UserResponseDTO convertToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getProfileImageUrl(),
                user.getDob(),
                user.getAddress()
        );
    }
}
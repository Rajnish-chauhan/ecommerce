package com.project.ecommerce.service;

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

    public UserResponseDTO registerNewUser(UserRequestDTO requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists!");
        }

        User user = new User();
        user.setName(requestDto.getName());
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRole(requestDto.getEmail().equalsIgnoreCase("admin@example.com") ? "ADMIN" : "USER");
        user.setVerified(true);

        User savedUser = userRepository.save(user);

        try {
            // We only pass the email and name now!
            emailService.sendRegistrationEmail(savedUser.getEmail(), savedUser.getName());
        } catch (Exception e) {
            System.err.println("❌ Registration email failed: " + e.getMessage());
        }

        return convertToResponseDTO(savedUser);
    }

    public UserResponseDTO loginUser(String email, String password) {
        User existingUser = userRepository.findByEmail(email);
        if (existingUser == null) {
            throw new ResourceNotFoundException("User not found! Please create an account.");
        }
        if (!passwordEncoder.matches(password, existingUser.getPassword())) {
            throw new SecurityException("Invalid Password");
        }
        return convertToResponseDTO(existingUser);
    }

    // ✅ ADDED BACK: Fetches user for Google OAuth login
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return convertToResponseDTO(user);
    }

    // ✅ ADDED BACK: Profile Update Logic
    public UserResponseDTO updateUser(String id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        existingUser.setName(updatedUser.getName());
        existingUser.setDob(updatedUser.getDob());
        existingUser.setAddress(updatedUser.getAddress());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        User savedUser = userRepository.save(existingUser);
        return convertToResponseDTO(savedUser);
    }

    public void processOAuthPostLogin(String email, String name, String picture) {
        if (userRepository.findByEmail(email) == null) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setProfileImageUrl(picture);
            newUser.setRole("USER");
            newUser.setVerified(true);
            userRepository.save(newUser);
            try {
                emailService.sendRegistrationEmail(email, name);
            } catch (Exception e) {
                System.err.println("❌ Google Welcome email failed: " + e.getMessage());
            }
        }
    }

    private UserResponseDTO convertToResponseDTO(User user) {
        // Includes dob and address for the profile page
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getProfileImageUrl(),
                user.getDob(),
                user.getAddress()
        );
    }
}
package com.project.ecommerce.controller;

import com.project.ecommerce.model.User;
import com.project.ecommerce.repo.UserRepository;
import com.project.ecommerce.service.EmailService;
import com.project.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;
    @Autowired
    private UserService userService;

    // Inject PasswordEncoder
    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already exists!"));
        }

        // Encrypt password and save user
        String rawPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("USER");
        user.setVerified(true);

        User savedUser = userRepository.save(user);

        //  Account successfully create hone par Welcome Email bhejein
        try {
            emailService.sendRegistrationEmail(savedUser.getEmail(), savedUser.getName(), rawPassword);
            System.out.println("✅ Manual Signup Welcome email triggered!");
        } catch (Exception e) {
            System.err.println("❌ Manual Signup email failed: " + e.getMessage());
        }

        return ResponseEntity.ok(Map.of("message", "User registered successfully", "user", savedUser));
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        // Find user by email
        User existingUser = userRepository.findByEmail(email);

        // validation: if user not found send error
        if (existingUser == null) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found! Please create an account."));
        }

        // Password Check

        if (!passwordEncoder.matches(password, existingUser.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid Password"));
        }

        // Success - Return user data back to frontend
        return ResponseEntity.ok(existingUser);
    }

    //  Profile Update Endpoint
    @PutMapping("/update/{id}")
    public User updateUser(@PathVariable String id, @RequestBody User updatedUser) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser != null) {
            existingUser.setName(updatedUser.getName());
            existingUser.setDob(updatedUser.getDob());
            existingUser.setAddress(updatedUser.getAddress());

            //  Encode the newly provided password if it exists
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            existingUser.setProfileImageUrl(updatedUser.getProfileImageUrl());
            return userRepository.save(existingUser);
        }
        return null;
    }

    @GetMapping("/by-email")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(user); // Pura user object JSON me bhej dega (including photo)
        }
        return ResponseEntity.status(404).body(Map.of("message", "User not found"));
    }
}
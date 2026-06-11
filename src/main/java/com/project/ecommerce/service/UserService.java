package com.project.ecommerce.service;

import com.project.ecommerce.model.User;
import com.project.ecommerce.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        try {
            if (user.getEmail() != null && user.getEmail().equalsIgnoreCase("admin@example.com")) {
                user.setRole("ADMIN");
            } else {
                user.setRole("USER");
            }

            // Store the raw password temporarily to send in the email
            String rawPassword = user.getPassword();

            if (rawPassword != null) {
                user.setPassword(passwordEncoder.encode(rawPassword));
            }

            User newUser = userRepository.save(user);
            System.out.println("User Added to database with role: " + newUser.getRole());

            try {
                // Send the raw password in the email, NOT the hashed one
                if (rawPassword != null) {
                    emailService.sendRegistrationEmail(newUser.getEmail(), newUser.getName(), rawPassword);
                }
            } catch (Exception e) {
                System.err.println("❌ Registration Email failed: " + e.getMessage());
            }

            return newUser;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public User loginUser(String email, String password) {
        User user = userRepository.findByEmail(email);

        if(user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null; // invalid credentials
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
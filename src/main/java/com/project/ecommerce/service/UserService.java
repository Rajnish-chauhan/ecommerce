package com.project.ecommerce.service;

import com.project.ecommerce.model.User;
import com.project.ecommerce.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;
    public User registerUser(User user) {
        try {

            if (user.getEmail() != null && user.getEmail().equalsIgnoreCase("admin@example.com")) {
                user.setRole("ADMIN");
            } else {
                user.setRole("USER");
            }

            if (user.getEmail() != null && user.getEmail().equalsIgnoreCase("admin@example.com")) {
                user.setRole("ADMIN");
            } else {
                user.setRole("USER");
            }

            User newUser = userRepository.save(user);
            System.out.println("User Added to database with role: " + newUser.getRole());


            try {
                if (newUser.getPassword() != null) {
                    emailService.sendRegistrationEmail(newUser.getEmail(), newUser.getName(), newUser.getPassword());
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
        if(user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null; // invalid credentials
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
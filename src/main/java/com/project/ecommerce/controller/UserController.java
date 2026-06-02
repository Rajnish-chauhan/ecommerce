package com.project.ecommerce.controller;

import com.project.ecommerce.model.User;
import com.project.ecommerce.repo.UserRepository;
import com.project.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }


    @PostMapping("/login")
    public User loginUser(@RequestBody Map<String, String> credentials) {
        return userService.loginUser(credentials.get("email"), credentials.get("password"));
    }

    //  Profile Update Endpoint
    @PutMapping("/update/{id}")
    public User updateUser(@PathVariable String id, @RequestBody User updatedUser) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser != null) {
            existingUser.setName(updatedUser.getName());
            existingUser.setDob(updatedUser.getDob());
            existingUser.setAddress(updatedUser.getAddress());

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                existingUser.setPassword(updatedUser.getPassword());
            }

            existingUser.setProfileImageUrl(updatedUser.getProfileImageUrl());
            return userRepository.save(existingUser);
        }
        return null;
    }

    //  Get Current User for Google Login
    @GetMapping("/get-by-email")
    public User getUserByEmail(@RequestParam String email) {
        return userRepository.findByEmail(email);
    }
}
package com.project.ecommerce.controller;

import com.project.ecommerce.dto.UserProfileUpdateDTO;
import com.project.ecommerce.dto.UserRequestDTO;
import com.project.ecommerce.dto.UserResponseDTO;
import com.project.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

    // Check email availability before dispatching registration OTP
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmailExists(@RequestParam String email) {
        boolean exists = userService.isEmailRegistered(email);
        return ResponseEntity.ok(Map.of(
                "exists", exists,
                "message", exists ? "An account with this email address already exists." : "Email is available."
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO userDto) {
        return ResponseEntity.ok(userService.registerNewUser(userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> loginUser(@RequestBody Map<String, String> loginData) {
        return ResponseEntity.ok(userService.loginUser(loginData.get("email"), loginData.get("password")));
    }

    @GetMapping("/by-email")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable String id, @RequestBody UserProfileUpdateDTO updateDto) {
        return ResponseEntity.ok(userService.updateUserProfile(id, updateDto));
    }

    // Permanently delete user account
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String id) {
        userService.deleteUserAccount(id);
        return ResponseEntity.ok(Map.of("message", "Account successfully deleted."));
    }
}
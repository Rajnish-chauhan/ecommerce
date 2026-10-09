package com.project.ecommerce.controller;

import com.project.ecommerce.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/otp")
@CrossOrigin("*")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email address is required."));
        }
        otpService.generateAndSendOtp(email.trim().toLowerCase());
        return ResponseEntity.ok(Map.of("message", "Verification OTP sent successfully."));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        if (email == null || otp == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email and OTP are required."));
        }
        boolean verified = otpService.verifyOtp(email.trim().toLowerCase(), otp.trim());
        if (!verified) {
            return ResponseEntity.status(400).body(Map.of("message", "Invalid or expired OTP."));
        }
        return ResponseEntity.ok(Map.of("message", "Email verified successfully.", "verified", true));
    }
}
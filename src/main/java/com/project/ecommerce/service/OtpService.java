package com.project.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private EmailService emailService;

    // Simple in-memory storage for OTPs.
    // Key: Email, Value: OTP
    private Map<String, String> otpStorage = new HashMap<>();

    public void generateAndSendOtp(String email) {
        // Generate a 6-digit random OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        // Store it and send it
        otpStorage.put(email, otp);
        emailService.sendOtpEmail(email, otp);
    }

    public boolean verifyOtp(String email, String inputOtp) {
        if (inputOtp != null && inputOtp.equals(otpStorage.get(email))) {
            otpStorage.remove(email); // Clear OTP after successful verification
            return true;
        }
        return false;
    }
}
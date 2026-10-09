package com.project.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    @Autowired
    private EmailService emailService;

    // 10 minutes OTP validity duration in milliseconds
    private static final long OTP_VALIDITY_DURATION_MS = 10 * 60 * 1000;

    // Inner class holding OTP code and expiration timestamp
    private static class OtpEntry {
        private final String otpCode;
        private final long expiryTime;

        public OtpEntry(String otpCode, long expiryTime) {
            this.otpCode = otpCode;
            this.expiryTime = expiryTime;
        }

        public String getOtpCode() {
            return otpCode;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    // Thread-safe in-memory cache for OTP records
    private final Map<String, OtpEntry> otpStorage = new ConcurrentHashMap<>();

    public void generateAndSendOtp(String rawEmail) {
        if (rawEmail == null || rawEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Email address cannot be empty.");
        }

        String normalizedEmail = rawEmail.trim().toLowerCase();

        // Generate a 6-digit numeric OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        long expiry = System.currentTimeMillis() + OTP_VALIDITY_DURATION_MS;

        otpStorage.put(normalizedEmail, new OtpEntry(otp, expiry));
        System.out.println("Generated OTP for " + normalizedEmail + ": " + otp);

        emailService.sendOtpEmail(normalizedEmail, otp);
    }

    public boolean verifyOtp(String rawEmail, String rawInputOtp) {
        if (rawEmail == null || rawInputOtp == null) {
            return false;
        }

        String normalizedEmail = rawEmail.trim().toLowerCase();
        String normalizedOtp = rawInputOtp.trim();

        OtpEntry entry = otpStorage.get(normalizedEmail);
        if (entry == null) {
            System.err.println("OTP verification failed: No active OTP found for " + normalizedEmail);
            return false;
        }

        if (entry.isExpired()) {
            otpStorage.remove(normalizedEmail);
            System.err.println("OTP verification failed: OTP expired for " + normalizedEmail);
            return false;
        }

        if (entry.getOtpCode().equals(normalizedOtp)) {
            otpStorage.remove(normalizedEmail); // Invalidate immediately upon successful use
            System.out.println("OTP verified successfully for " + normalizedEmail);
            return true;
        }

        System.err.println("OTP verification failed: Code mismatch for " + normalizedEmail);
        return false;
    }
}
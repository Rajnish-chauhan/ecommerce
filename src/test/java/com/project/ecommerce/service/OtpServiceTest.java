package com.project.ecommerce.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock(lenient = true)
    private MongoTemplate mongoTemplate;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OtpService otpService;

    @Test
    @DisplayName("Should generate and send OTP email successfully")
    void testGenerateAndSendOtp_Success() {
        String email = "testuser@example.com";

        // Act
        otpService.generateAndSendOtp(email);

        // Assert that the email service was invoked with the target email and a generated OTP
        verify(emailService, times(1)).sendOtpEmail(eq(email), anyString());
    }

    @Test
    @DisplayName("Should successfully verify OTP when matching code is entered")
    void testVerifyOtp_Success() {
        String email = "testuser@example.com";

        // 1. Trigger OTP generation
        otpService.generateAndSendOtp(email);

        // 2. Capture the actual 6-digit OTP code sent via EmailService
        ArgumentCaptor<String> otpCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService).sendOtpEmail(eq(email), otpCaptor.capture());
        String generatedOtp = otpCaptor.getValue();

        // 3. Verify with the captured code
        boolean isVerified = otpService.verifyOtp(email, generatedOtp);

        // Assert verification succeeded
        assertTrue(isVerified, "Verification should succeed for the generated OTP code.");
    }

    @Test
    @DisplayName("Should reject invalid OTP code")
    void testVerifyOtp_InvalidCode() {
        String email = "testuser@example.com";

        // 1. Trigger OTP generation
        otpService.generateAndSendOtp(email);

        // 2. Attempt verification with an intentionally wrong code
        boolean isVerified = otpService.verifyOtp(email, "000000");

        // Assert verification failed
        assertFalse(isVerified, "Verification should fail for an incorrect OTP code.");
    }

    @Test
    @DisplayName("Should reject verification when email is empty or null")
    void testVerifyOtp_NullEmail() {
        boolean isVerified = otpService.verifyOtp(null, "123456");
        assertFalse(isVerified, "Verification should fail when email is null.");
    }
}
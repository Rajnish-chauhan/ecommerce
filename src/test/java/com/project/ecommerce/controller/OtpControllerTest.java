package com.project.ecommerce.controller;

import com.project.ecommerce.service.OtpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OtpControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OtpService otpService;

    @InjectMocks
    private OtpController otpController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(otpController).build();
    }

    @Test
    @DisplayName("POST /api/otp/send - Should accept email and return 200 OK")
    void testSendOtp() throws Exception {
        String jsonPayload = """
            {
                "email": "client@example.com"
            }
            """;

        mockMvc.perform(post("/api/otp/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Verification OTP sent successfully."));
    }

    @Test
    @DisplayName("POST /api/otp/verify - Should return 200 OK when code matches")
    void testVerifyOtp_Success() throws Exception {
        when(otpService.verifyOtp("client@example.com", "123456")).thenReturn(true);

        String jsonPayload = """
            {
                "email": "client@example.com",
                "otp": "123456"
            }
            """;

        mockMvc.perform(post("/api/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(true));
    }

    @Test
    @DisplayName("POST /api/otp/verify - Should return 400 Bad Request when code is invalid")
    void testVerifyOtp_Failure() throws Exception {
        when(otpService.verifyOtp("client@example.com", "999999")).thenReturn(false);

        String jsonPayload = """
            {
                "email": "client@example.com",
                "otp": "999999"
            }
            """;

        mockMvc.perform(post("/api/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid or expired OTP."));
    }
}
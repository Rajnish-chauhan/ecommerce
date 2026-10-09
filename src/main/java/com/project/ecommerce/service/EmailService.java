package com.project.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${admin.alert.email:rc303604@gmail.com}")
    private String adminEmail;

    public void sendOrderConfirmationEmail(String toEmail, String userName, String orderId, double totalAmount) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);
            message.setTo(toEmail);
            message.setSubject("Order Confirmation - SastaHai");

            String emailBody = "Hello " + userName + ",\n\n"
                    + "Thank you for shopping with SastaHai.\n"
                    + "Your payment was successful and your order has been placed.\n\n"
                    + "Order Details:\n"
                    + "Order ID: #" + orderId + "\n"
                    + "Total Amount Paid: INR " + totalAmount + "\n\n"
                    + "We will notify you once your order is dispatched.\n\n"
                    + "Best regards,\n"
                    + "SastaHai Support Team";

            message.setText(emailBody);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending Order Confirmation Email: " + e.getMessage());
        }
    }

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);
            message.setTo(toEmail); // Fixed: sending to actual recipient
            message.setSubject("Account Verification OTP - SastaHai");

            String emailBody = "Hello,\n\n"
                    + "Thank you for registering with SastaHai.\n"
                    + "Your One-Time Password (OTP) for account verification is: " + otp + "\n\n"
                    + "This code is valid for 10 minutes. Please do not share this OTP with anyone.\n\n"
                    + "Best regards,\n"
                    + "SastaHai Security Team";

            message.setText(emailBody);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending Registration OTP Email: " + e.getMessage());
        }
    }

    public void sendOrderAlertToAdmin(String userEmail, String userName, String orderId, double totalAmount) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);
            message.setTo(adminEmail);
            message.setReplyTo(userEmail);
            message.setSubject("New Order Notification: Order #" + orderId);

            String emailBody = "Hello Admin,\n\n"
                    + "A new order has been placed on SastaHai.\n\n"
                    + "Customer Name: " + userName + "\n"
                    + "Customer Email: " + userEmail + "\n"
                    + "Order ID: #" + orderId + "\n"
                    + "Total Amount: INR " + totalAmount + "\n\n"
                    + "Reply directly to this email to reach the customer.";

            message.setText(emailBody);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending Admin Alert Email: " + e.getMessage());
        }
    }

    public void sendRegistrationEmail(String to, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);
            message.setTo(to);
            message.setSubject("Welcome to SastaHai - Account Created");

            String body = "Hello " + name + ",\n\n"
                    + "Welcome to SastaHai. Your account has been registered successfully.\n\n"
                    + "Registered Email: " + to + "\n\n"
                    + "You can manage your orders and profile anytime from your dashboard.\n\n"
                    + "Best regards,\n"
                    + "SastaHai Team";

            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending registration email: " + e.getMessage());
        }
    }
}
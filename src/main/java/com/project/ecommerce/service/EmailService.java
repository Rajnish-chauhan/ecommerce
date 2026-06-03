package com.project.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Order Confirmation Email
    public void sendOrderConfirmationEmail(String toEmail, String userName, String orderId, double totalAmount) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("crajnish425@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Order Confirmation - SastaHai ⚡");

            String emailBody = "Hello " + userName + ",\n\n"
                    + "Thank you for shopping with SastaHai!\n"
                    + "Your payment was successful and your order has been placed.\n\n"
                    + "Order Details:\n"
                    + "Order ID: #" + orderId + "\n"
                    + "Total Amount Paid: ₹" + totalAmount + "\n\n"
                    + "We will notify you once your order is shipped.\n\n"
                    + "Best Regards,\n"
                    + "SastaHai Team";

            message.setText(emailBody);
            mailSender.send(message);
            System.out.println("✅ Order Confirmation Email sent to: " + toEmail);
        } catch (Exception e) {
            System.out.println("❌ Error sending Order Confirmation Email: " + e.getMessage());
        }
    }

    // Admin Order Alert Email
    public void sendOrderAlertToAdmin(String userEmail, String userName, String orderId, double totalAmount) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("crajnish425@gmail.com");
            message.setTo("rc303604@gmail.com");
            message.setReplyTo(userEmail);
            message.setSubject("🚨 New Order Received from: " + userName);

            String emailBody = "Hello Admin,\n\n"
                    + "A new order has been placed on SastaHai!\n\n"
                    + "Customer Name: " + userName + "\n"
                    + "Customer Email: " + userEmail + "\n"
                    + "Order ID: #" + orderId + "\n"
                    + "Total Amount: ₹" + totalAmount + "\n\n"
                    + "💡 Note: You can reply directly to this email to contact the customer.";

            message.setText(emailBody);
            mailSender.send(message);
            System.out.println("✅ Admin Alert Email sent for Order ID: " + orderId);
        } catch (Exception e) {
            System.out.println("❌ Error sending Admin Alert Email: " + e.getMessage());
        }
    }

    // Registration Email
    public void sendRegistrationEmail(String toEmail, String userName, String password) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("crajnish425@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Welcome to SastaHai ⚡ - Account Created");

            String emailBody = "Hello " + userName + ",\n\n"
                    + "Thank you for signing up on SastaHai!\n"
                    + "Your account has been successfully created.\n\n"
                    + "Your Login Details:\n"
                    + "Email: " + toEmail + "\n"
                    + "Password: " + password + "\n\n"
                    + "Please keep your password safe. You can update your profile anytime.\n\n"
                    + "Best Regards,\n"
                    + "SastaHai Team";

            message.setText(emailBody);
            mailSender.send(message);
            System.out.println("✅ Registration Email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.out.println("❌ Error in sending Registration Email: " + e.getMessage());
        }
    }
}
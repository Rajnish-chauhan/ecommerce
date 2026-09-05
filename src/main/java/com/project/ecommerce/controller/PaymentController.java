package com.project.ecommerce.controller;

import com.project.ecommerce.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin("*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> data) {
        try {

            Number amountNumber = (Number) data.get("amount");
            int amount = amountNumber.intValue();

            String orderId = paymentService.createRazorpayOrder(amount);


            return ResponseEntity.ok(Map.of("id", orderId));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Payment initiation failed: " + e.getMessage()));
        }
    }
}
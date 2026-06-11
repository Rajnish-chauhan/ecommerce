package com.project.ecommerce.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
// Soft-coded origin injection
@CrossOrigin(origins = "${api.external-service.url}", allowCredentials = "true")
public class PaymentController {

    @Value("${RAZORPAY_ID_TEST}")
    private String razorpayId;

    @Value("${RAZORPAY_SECRET_TEST}")
    private String razorpaySecret;

    @PostMapping("/create-order")
    public String createOrder(@RequestBody Map<String, Object> data) throws RazorpayException {
        int amount = (int) data.get("amount"); // Amount should be passed in smallest unit (e.g., paise)

        RazorpayClient client = new RazorpayClient(razorpayId, razorpaySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); // Multiply by 100 to convert INR to paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_123456");

        Order order = client.orders.create(orderRequest);
        return order.toString(); // Returns the Razorpay order details (including order_id) to the frontend
    }
}
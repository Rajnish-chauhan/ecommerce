package com.project.ecommerce.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Value("${RAZORPAY_ID_TEST}")
    private String razorpayId;

    @Value("${RAZORPAY_SECRET_TEST}")
    private String razorpaySecret;

    public String createRazorpayOrder(int amountInRupees) throws RazorpayException {
        RazorpayClient client = new RazorpayClient(razorpayId, razorpaySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInRupees * 100);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

        Order order = client.orders.create(orderRequest);

        return order.get("id");
    }
}
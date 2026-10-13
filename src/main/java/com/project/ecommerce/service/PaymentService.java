package com.project.ecommerce.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    // Resolves Razorpay Key ID across profiles with chained fallbacks to prevent startup crash
    @Value("${razorpay.key.id:${RAZORPAY_KEY_ID:${RAZORPAY_ID_PROD:${RAZORPAY_ID_TEST:rzp_test_dummy}}}}")
    private String razorpayId;

    // Resolves Razorpay Secret across profiles with chained fallbacks to prevent startup crash
    @Value("${razorpay.key.secret:${RAZORPAY_KEY_SECRET:${RAZORPAY_SECRET_PROD:${RAZORPAY_SECRET_TEST:dummy_secret}}}}")
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
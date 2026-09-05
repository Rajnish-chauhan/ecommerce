package com.project.ecommerce.service;

import com.project.ecommerce.dto.OrderDTO;
import com.project.ecommerce.dto.OrderItemDTO;
import com.project.ecommerce.exception.ResourceNotFoundException;
import com.project.ecommerce.model.OrderItem;
import com.project.ecommerce.model.Orders;
import com.project.ecommerce.model.Product;
import com.project.ecommerce.model.User;
import com.project.ecommerce.repo.OrderRepository;
import com.project.ecommerce.repo.ProductRepository;
import com.project.ecommerce.repo.UserRepository;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OrderRepository orderRepository;

    @Value("${RAZORPAY_SECRET_TEST}")
    private String razorpaySecret;

    public OrderDTO placeOrder(String userId, Map<String, Integer> productQuantities, double totalAmount,
                               String paymentId, String razorpayOrderId, String signature) {

        // 1. IDEMPOTENCY CHECK: Prevent double-saving the same order
        if (orderRepository.existsByRazorpayOrderId(razorpayOrderId)) {
            throw new IllegalArgumentException("Order has already been processed for this payment.");
        }

        // 2. SECURITY CHECK: Verify Razorpay Signature
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", razorpayOrderId);
            options.put("razorpay_payment_id", paymentId);
            options.put("razorpay_signature", signature);

            boolean isValid = Utils.verifyPaymentSignature(options, razorpaySecret);
            if (!isValid) {
                throw new SecurityException("Payment signature verification failed! Possible fraudulent attempt.");
            }
        } catch (Exception e) {
            throw new SecurityException("Error validating payment signature.", e);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Orders order = new Orders();
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setStatus("Confirmed");
        order.setTotalAmount(totalAmount);
        order.setRazorpayOrderId(razorpayOrderId); // ✅ Save the Razorpay ID

        List<OrderItem> orderItems = new ArrayList<>();
        List<OrderItemDTO> orderItemDTOS = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : productQuantities.entrySet()) {
            Product product = productRepository.findById(entry.getKey())
                    .orElseThrow(() -> new ResourceNotFoundException("Product Not found"));

            int requestedQuantity = entry.getValue();
            if (product.getStock() < requestedQuantity) {
                throw new IllegalArgumentException("Out of stock for product: " + product.getName());
            }

            product.setStock(product.getStock() - requestedQuantity);
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(requestedQuantity);
            orderItems.add(orderItem);

            orderItemDTOS.add(new OrderItemDTO(product.getName(), product.getPrice(), requestedQuantity));
        }

        order.setOrderItems(orderItems);
        Orders saveOrder = orderRepository.save(order);

        try {
            emailService.sendOrderConfirmationEmail(user.getEmail(), user.getName(), saveOrder.getId(), saveOrder.getTotalAmount());
            emailService.sendOrderAlertToAdmin(user.getEmail(), user.getName(), saveOrder.getId(), saveOrder.getTotalAmount());
        } catch (Exception e) {
            System.err.println("❌ Email sending failed. Error: " + e.getMessage());
        }

        return new OrderDTO(saveOrder.getId(), saveOrder.getTotalAmount(),
                saveOrder.getStatus(), saveOrder.getOrderDate(), orderItemDTOS);
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<OrderDTO> getOrderByUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return orderRepository.findByUser(user).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private OrderDTO convertToDTO(Orders orders) {
        List<OrderItemDTO> OrderItems = orders.getOrderItems().stream()
                .map(item -> new OrderItemDTO(item.getProduct().getName(), item.getProduct().getPrice(), item.getQuantity()))
                .collect(Collectors.toList());
        return new OrderDTO(orders.getId(), orders.getTotalAmount(), orders.getStatus(), orders.getOrderDate(),
                orders.getUser() != null ? orders.getUser().getName() : "Unknown",
                orders.getUser() != null ? orders.getUser().getEmail() : "Unknown", OrderItems);
    }
}
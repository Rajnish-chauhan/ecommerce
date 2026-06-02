package com.project.ecommerce.service;

import com.project.ecommerce.dto.OrderDTO;
import com.project.ecommerce.dto.OrderItemDTO;
import com.project.ecommerce.model.OrderItem;
import com.project.ecommerce.model.Orders;
import com.project.ecommerce.model.Product;
import com.project.ecommerce.model.User;

import com.project.ecommerce.repo.OrderRepository;
import com.project.ecommerce.repo.ProductRepository;
import com.project.ecommerce.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public OrderDTO placeOrder(String userId, Map<String, Integer> productQuantities, double totalAmount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Orders order = new Orders();
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setStatus("Confirmed");
        order.setTotalAmount(totalAmount);

        List<OrderItem> orderItems = new ArrayList<>();
        List<OrderItemDTO> orderItemDTOS = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : productQuantities.entrySet()) {
            Product product = productRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("Product Not found"));

            int requestedQuantity = entry.getValue();

            //  STOCK CHECK & DEDUCTION LOGIC
            if (product.getStock() < requestedQuantity) {
                throw new RuntimeException("Out of stock for product: " + product.getName());
            }
            product.setStock(product.getStock() - requestedQuantity);
            productRepository.save(product); // MongoDB mein naya stock save

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
            System.err.println("❌ Email sending failed, but order was saved. Error: " + e.getMessage());
        }

        return new OrderDTO(saveOrder.getId(), saveOrder.getTotalAmount(),
                saveOrder.getStatus(), saveOrder.getOrderDate(), orderItemDTOS);
    }

    public List<OrderDTO> getAllOrders() {
        List<Orders> orders = orderRepository.findAll();
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private OrderDTO convertToDTO(Orders orders) {
        List<OrderItemDTO> OrderItems = orders.getOrderItems().stream()
                .map(item -> new OrderItemDTO(
                        item.getProduct().getName(),
                        item.getProduct().getPrice(),
                        item.getQuantity())).collect(Collectors.toList());
        return new OrderDTO(
                orders.getId(), orders.getTotalAmount(), orders.getStatus(), orders.getOrderDate(),
                orders.getUser() != null ? orders.getUser().getName() : "Unknown",
                orders.getUser() != null ? orders.getUser().getEmail() : "Unknown",
                OrderItems
        );
    }

    public List<OrderDTO> getOrderByUser(String userId) {
        Optional<User> userOp = userRepository.findById(userId);
        if(userOp.isEmpty()) {
            throw new RuntimeException("user not found");
        }
        return orderRepository.findByUser(userOp.get()).stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}
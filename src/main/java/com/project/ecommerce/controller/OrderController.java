package com.project.ecommerce.controller;

import com.project.ecommerce.dto.OrderDTO;
import com.project.ecommerce.model.OrderRequest;
import com.project.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin("*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place/{userId}")
    public ResponseEntity<OrderDTO> placeOrder(@PathVariable String userId, @RequestBody OrderRequest orderRequest) {
        OrderDTO order = orderService.placeOrder(
                userId,
                orderRequest.getProductQuantities(),
                orderRequest.getTotalAmount(),
                orderRequest.getRazorpay_payment_id(),
                orderRequest.getRazorpay_order_id(),
                orderRequest.getRazorpay_signature()
        );
        return ResponseEntity.ok(order);
    }

    @GetMapping("/all-orders")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrderByUser(@PathVariable String userId) {
        return ResponseEntity.ok(orderService.getOrderByUser(userId));
    }
}
package com.project.ecommerce.controller;

import com.project.ecommerce.dto.OrderDTO;
import com.project.ecommerce.model.OrderRequest;
import com.project.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin("*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place/{userId}")
    public OrderDTO placeOrder(@PathVariable String userId, @RequestBody OrderRequest orderRequest) {
        return orderService.placeOrder(
                userId,
                orderRequest.getProductQuantities(),
                orderRequest.getTotalAmount(),
                orderRequest.getRazorpay_payment_id(),
                orderRequest.getRazorpay_order_id(),
                orderRequest.getRazorpay_signature()
        );
    }
//Get all orders
    @GetMapping("/all-orders")
    public List<OrderDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/user/{userId}")
    public List<OrderDTO> getOrderByUser(@PathVariable String userId) {
        return orderService.getOrderByUser(userId);
    }
}
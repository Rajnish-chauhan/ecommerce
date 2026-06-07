package com.project.ecommerce.service;

import com.project.ecommerce.dto.OrderDTO;
import com.project.ecommerce.model.Orders;
import com.project.ecommerce.model.Product;
import com.project.ecommerce.model.User; // Ensure this is your custom User model
import com.project.ecommerce.repo.OrderRepository;
import com.project.ecommerce.repo.ProductRepository;
import com.project.ecommerce.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private ProductRepository productRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private EmailService emailService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void testPlaceOrder_Success() {
        // Arrange
        User user = new User();
        user.setId("u1");
        user.setName("Rajnish");
        user.setEmail("rajnish@example.com");

        Product product = new Product();
        product.setId("p1");
        product.setStock(10);
        product.setName("Test Product");
        product.setPrice(50.0);

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));

        // Mock save to simulate database ID generation
        when(orderRepository.save(any(Orders.class))).thenAnswer(invocation -> {
            Orders order = invocation.getArgument(0);
            order.setId("order_123"); // Manually setting ID for the mock
            return order;
        });

        Map<String, Integer> quantities = Map.of("p1", 2);

        // Act
        OrderDTO result = orderService.placeOrder("u1", quantities, 100.0, "pay_1", "ord_1", "sig_1");

        // Assert
        assertNotNull(result);
        assertEquals(8, product.getStock()); // Stock should be 10 - 2 = 8
        verify(orderRepository, times(1)).save(any(Orders.class));
        verify(emailService, times(1)).sendOrderConfirmationEmail(anyString(), anyString(), anyString(), anyDouble());
    }
}
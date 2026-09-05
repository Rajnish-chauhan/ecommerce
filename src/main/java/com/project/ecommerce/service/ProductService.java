package com.project.ecommerce.service;

import com.project.ecommerce.model.Product;
import com.project.ecommerce.model.User;
import com.project.ecommerce.repo.ProductRepository;
import com.project.ecommerce.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository; // Needed here to verify admin role

    public Product addProduct(Product product, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new SecurityException("Unauthorized: Missing User Identification Header");
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null || !"ADMIN".equals(user.getRole())) {
            throw new SecurityException("Forbidden: Access denied. Only admins can add products.");
        }

        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(String id) {
        return productRepository.findById(id).orElse(null);
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }
}
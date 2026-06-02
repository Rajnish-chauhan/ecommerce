package com.project.ecommerce.controller;

import com.project.ecommerce.model.Product;
import com.project.ecommerce.repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "https://ecommerce.rajnishsystems.in/", allowCredentials = "true")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // 1. Get all products
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 2. Add a new product from the React UI
    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product savedProduct = productRepository.save(product);
        return ResponseEntity.ok(savedProduct);
    }
}
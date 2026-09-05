package com.project.ecommerce.controller;

import com.project.ecommerce.model.Product;
import com.project.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@CrossOrigin("*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(
            @RequestBody Product product,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            Product savedProduct = productService.addProduct(product, userId);
            return ResponseEntity.ok(savedProduct);
        } catch (SecurityException e) {
            int status = e.getMessage().contains("Unauthorized") ? 401 : 403;
            return ResponseEntity.status(status).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable String id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        }
        return ResponseEntity.status(404).body(Map.of("message", "Product not found"));
    }
}
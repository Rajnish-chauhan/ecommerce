package com.project.ecommerce.controller;

import com.project.ecommerce.model.Product;
import com.project.ecommerce.model.User;
import com.project.ecommerce.repo.ProductRepository;
import com.project.ecommerce.repo.UserRepository;
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
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // SECURED ADMIN ENDPOINT: Product add karne ke liye
    @PostMapping("/add")
    public ResponseEntity<?> addProduct(
            @RequestBody Product product,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        // Validation: Check agar header khali hai ya missing hai
        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized: Missing User Identification Header"));
        }

        // Database se fetch karke user ka role check karo
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(403).body(Map.of("message", "Forbidden: Access denied. Only admins can add products."));
        }

        // Agar user ADMIN hai, toh product MongoDB mein save karo
        Product savedProduct = productRepository.save(product);
        return ResponseEntity.ok(savedProduct);
    }

    // 2. PUBLIC ENDPOINT: Saare products fetch karne ke liye (Sabhi use kar sakte hain)
    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(products);
    }

    // 3. PUBLIC ENDPOINT: Single product detail ID ke base par nikalne ke liye
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable String id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body((Product) Map.of("message", "Product not found")));
    }


}
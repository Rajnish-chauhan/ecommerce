package com.project.ecommerce;

import com.project.ecommerce.model.Product;
import com.project.ecommerce.repo.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class EcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(ProductRepository productRepository) {
        return args -> {
            System.out.println("Checking and injecting default premium inventory...");

            List<Product> defaultProducts = Arrays.asList(
                    // 💻 ELECTRONICS
                    createProduct("MacBook Pro M3", "Apple 14-inch Laptop, 512GB SSD", 16990,
                            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=800&q=80", "Electronics"),

                    createProduct("iPhone 15 Pro", "Titanium Design, 256GB", 13490,
                            "https://images.unsplash.com/photo-1696446701796-da61225697cc?auto=format&fit=crop&w=800&q=80", "Electronics"),

                    createProduct("Sony WH-1000XM5", "Wireless Noise Cancelling Headphones", 2999,
                            "https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?auto=format&fit=crop&w=800&q=80", "Electronics"),

                    createProduct("Apple Watch Series 9", "Smartwatch with Health Tracking", 4190,
                            "https://images.unsplash.com/photo-1434493789847-2f02dc6ca35d?auto=format&fit=crop&w=800&q=80", "Electronics"),

                    createProduct("Logitech MX Master 3S", "Wireless Performance Mouse", 899,
                            "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?auto=format&fit=crop&w=800&q=80", "Electronics"),

                    // 👕 CLOTHING
                    createProduct("Levi's 511 Slim Jeans", "Men's Premium Denim", 259,
                            "https://images.unsplash.com/photo-1542272604-787c3835535d?auto=format&fit=crop&w=800&q=80", "Cloth"),

                    createProduct("Nike Air Force 1", "Classic White Sneakers", 849,
                            "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?auto=format&fit=crop&w=800&q=80", "Cloth"),

                    createProduct("Zara Cotton T-Shirt", "Solid Black Crew Neck", 99,
                            "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=800&q=80", "Cloth"),

                    createProduct("Columbia Winter Jacket", "Waterproof Snow Jacket", 999,
                            "https://images.unsplash.com/photo-1551028719-00167b16eac5?auto=format&fit=crop&w=800&q=80", "Cloth"),

                    createProduct("Ray-Ban Aviators", "Classic Gold Frame Sunglasses", 659,
                            "https://images.unsplash.com/photo-1511499767150-a48a237f0083?auto=format&fit=crop&w=800&q=80", "Cloth"),

                    // 🛒 GROCERIES
                    createProduct("Fresh Red Apples", "1kg Farm Fresh, Premium Quality", 25,
                            "https://www.paperandtea.com/cdn/shop/articles/Apfel_7ebe153a-a4ac-473a-9217-658894dfc968.jpg?v=1765535477&width=1500", "Grocery"),

                    createProduct("Organic Tomatoes", "1kg Fresh and Juicy", 8,
                            "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?auto=format&fit=crop&w=800&q=80", "Grocery"),

                    createProduct("Whole Wheat Bread", "Freshly Baked, 400g", 5,
                            "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=800&q=80", "Grocery"),

                    createProduct("Nescafe Classic Coffee", "Instant Coffee - 100g", 29,
                            "https://images.unsplash.com/photo-1559525839-b184a4d698c7?auto=format&fit=crop&w=800&q=80", "Grocery"),

                    createProduct("Almonds (Badam)", "Premium California Almonds - 500g", 69,
                            "https://images.unsplash.com/photo-1508061253366-f7da158b6d46?auto=format&fit=crop&w=800&q=80", "Grocery")
            );

            // 🔥 SMART INJECTION: Jo product DB mein nahi hai, sirf wahi save hoga
            for (Product p : defaultProducts) {
                if (!productRepository.existsByName(p.getName())) {
                    productRepository.save(p);
                    System.out.println("-> Injected from backend: " + p.getName());
                }
            }

            System.out.println("✅ Backend product initialization check complete!");
        };
    }

    private Product createProduct(String name, String desc, double price, String imgUrl, String category) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(desc);
        p.setPrice(price);
        p.setImageUrl(imgUrl);
        p.setCategory(category);


        p.setStock(50);

        return p;
    }
}
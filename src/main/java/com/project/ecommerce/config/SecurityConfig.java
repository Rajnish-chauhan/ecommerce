package com.project.ecommerce.config;

import com.project.ecommerce.model.User;
import com.project.ecommerce.repo.UserRepository;
import com.project.ecommerce.service.EmailService;
import com.project.ecommerce.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;


    @Autowired
    private OtpService otpService;

    // Injecting the soft-coded frontend URL
    @Value("${api.external-service.url}")
    private String frontendUrl;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS Setup
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Disable CSRF for POST requests
                .csrf(csrf -> csrf.disable())

                // Whitelist URLs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/products/**", "/users/**", "/api/payment/**", "/orders/**", "/login/**", "/oauth2/**", "/api/otp/**").permitAll()
                        .anyRequest().authenticated()
                )

                // OAUTH2 SUCCESS HANDLER
                .oauth2Login(oauth2 -> oauth2
                        .successHandler((request, response, authentication) -> {
                            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
                            String email = oauthUser.getAttribute("email");
                            String name = oauthUser.getAttribute("name");
                            String picture = oauthUser.getAttribute("picture");

                            User existingUser = userRepository.findByEmail(email);

                            if (existingUser == null) {
                                // New account create
                                User newUser = new User();
                                newUser.setEmail(email);
                                newUser.setName(name);
                                newUser.setProfileImageUrl(picture);
                                newUser.setRole("USER");
                                newUser.setVerified(true); // Google users ko direct verified (true) mark karo
                                userRepository.save(newUser);

                                // First time account creation par Welcome Email bhejein
                                try {
                                    emailService.sendRegistrationEmail(email, name, "Logged in securely via Google");
                                    System.out.println("✅ Google Welcome email sent successfully!");
                                } catch (Exception e) {
                                    System.err.println("❌ Google Welcome email failed: " + e.getMessage());
                                }
                            }
                            response.sendRedirect(frontendUrl + "/?email=" + email);
                        })
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Use soft-coded frontend URL for allowed origins
        configuration.setAllowedOrigins(Arrays.asList(frontendUrl));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 🔥 FIX: Yahan par 'X-User-Id' add kar diya hai 🔥
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-User-Id"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
package com.project.ecommerce.config;

import com.project.ecommerce.model.User;
import com.project.ecommerce.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
    private UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //  CORS Setup
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                //  Disable CSRF for POST requests
                .csrf(csrf -> csrf.disable())

                // Whitelist URLs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/products/**", "/users/**", "/api/payment/**", "/orders/**", "/login/**", "/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )

                //  OAUTH2 SUCCESS HANDLER
                .oauth2Login(oauth2 -> oauth2
                        .successHandler((request, response, authentication) -> {
                            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
                            String email = oauthUser.getAttribute("email");
                            String name = oauthUser.getAttribute("name");
                            String picture = oauthUser.getAttribute("picture");

                            // save auth user
                            User existingUser = userRepository.findByEmail(email);
                            if (existingUser == null) {
                                User newUser = new User();
                                newUser.setEmail(email);
                                newUser.setName(name);
                                newUser.setProfileImageUrl(picture);
                                newUser.setRole("USER");
                                userRepository.save(newUser);
                            }

                            // redirect frontend send email
                            response.sendRedirect("https://ecommerce.rajnishsystems.in/?email=" + email);
                        })
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        //allow origin to connect
        configuration.setAllowedOrigins(Arrays.asList("https://ecommerce.rajnishsystems.in", "http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
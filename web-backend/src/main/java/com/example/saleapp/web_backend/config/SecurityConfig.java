package com.example.saleapp.web_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration
 * 
 * Provides BCrypt password encoder for secure password hashing
 * Disables default Spring Security form login for REST API
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * BCrypt password encoder bean with strength 12 (2^12 = 4096 rounds)
     * Used for hashing passwords during registration and verifying during login
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Security filter chain configuration
     * Permits all requests to API endpoints (REST API doesn't use form login)
     * CORS is handled separately in CorsConfig
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for REST API
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Allow all requests (authentication handled manually)
            )
            .formLogin(form -> form.disable()) // Disable form login UI
            .httpBasic(basic -> basic.disable()); // Disable HTTP Basic auth popup
        return http.build();
    }
}

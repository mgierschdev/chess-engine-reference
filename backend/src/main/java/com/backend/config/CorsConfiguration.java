package com.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Centralized CORS configuration for the Chess API.
 * 
 * Allows frontend applications to make cross-origin requests to the backend.
 * The allowed origin can be configured via the 'cors.allowed-origin' property
 * or the CORS_ALLOWED_ORIGIN environment variable.
 * 
 * Default: http://localhost:3000 (local development)
 */
@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    @Value("${cors.allowed-origin:http://localhost:3000}")
    private String allowedOrigin;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigin)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}

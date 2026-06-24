package com.insurance.subscription_manager.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.insurance.subscription_manager.entity.Product;
import com.insurance.subscription_manager.repository.ProductRepository;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    // Allow the Angular dev server (localhost:4200) to call the API.
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    // Seed a minimal product catalogue at startup (H2 in-memory DB starts empty).
    @Bean
    CommandLineRunner seedProducts(ProductRepository productRepository) {
        return args -> {
            if (productRepository.count() == 0) {
                productRepository.save(Product.builder()
                        .code("AUTO").libelle("Assurance Auto").type("VEHICULE").build());
                productRepository.save(Product.builder()
                        .code("HAB").libelle("Assurance Habitation").type("HABITATION").build());
                productRepository.save(Product.builder()
                        .code("SANTE").libelle("Assurance Sante").type("SANTE").build());
                productRepository.save(Product.builder()
                        .code("VIE").libelle("Assurance Vie").type("VIE").build());
            }
        };
    }
}

package com.insurance.subscription_manager.mapper;

import org.springframework.stereotype.Component;

import com.insurance.subscription_manager.dto.ProductRequestDTO;
import com.insurance.subscription_manager.dto.ProductResponseDTO;
import com.insurance.subscription_manager.entity.Product;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequestDTO dto) {
        return Product.builder()
                .code(dto.code())
                .libelle(dto.libelle())
                .type(dto.type())
                .build();
    }

    public ProductResponseDTO toResponse(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getCode(),
                product.getLibelle(),
                product.getType()
        );
    }
}

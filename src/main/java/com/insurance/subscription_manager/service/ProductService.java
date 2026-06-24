package com.insurance.subscription_manager.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.insurance.subscription_manager.dto.ProductRequestDTO;
import com.insurance.subscription_manager.dto.ProductResponseDTO;
import com.insurance.subscription_manager.entity.Product;
import com.insurance.subscription_manager.exception.DuplicateResourceException;
import com.insurance.subscription_manager.mapper.ProductMapper;
import com.insurance.subscription_manager.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponseDTO create(ProductRequestDTO request) {
        if (productRepository.existsByCode(request.code())) {
            throw new DuplicateResourceException("Un produit existe deja avec le code " + request.code());
        }
        Product saved = productRepository.save(productMapper.toEntity(request));
        return productMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .toList();
    }
}

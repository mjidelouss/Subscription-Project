package com.insurance.subscription_manager.dto;

public record ProductResponseDTO(
        Long id,
        String code,
        String libelle,
        String type
) {
}

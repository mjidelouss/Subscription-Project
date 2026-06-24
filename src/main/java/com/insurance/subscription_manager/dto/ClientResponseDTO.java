package com.insurance.subscription_manager.dto;

public record ClientResponseDTO(
        Long id,
        String nom,
        String email,
        String telephone
) {
}

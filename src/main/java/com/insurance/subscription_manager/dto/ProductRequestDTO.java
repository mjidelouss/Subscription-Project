package com.insurance.subscription_manager.dto;

import jakarta.validation.constraints.NotBlank;

public record ProductRequestDTO(

        @NotBlank(message = "le code est obligatoire")
        String code,

        @NotBlank(message = "le libelle est obligatoire")
        String libelle,

        @NotBlank(message = "le type est obligatoire")
        String type
) {
}

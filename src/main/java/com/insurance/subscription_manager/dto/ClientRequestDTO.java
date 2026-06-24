package com.insurance.subscription_manager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Incoming payload to create a client.
 * Matches the spec example: { nom, email, telephone }.
 */
public record ClientRequestDTO(

        @NotBlank(message = "le nom est obligatoire")
        String nom,

        @NotBlank(message = "l'email est obligatoire")
        @Email(message = "l'email n'est pas valide")
        String email,

        @NotBlank(message = "le telephone est obligatoire")
        String telephone
) {
}

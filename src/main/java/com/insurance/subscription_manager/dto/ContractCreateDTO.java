package com.insurance.subscription_manager.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

/**
 * Incoming payload to create a contract.
 * Matches the spec example: { quoteId, dateEffet }.
 */
public record ContractCreateDTO(

        @NotNull(message = "quoteId est obligatoire")
        Long quoteId,

        @NotNull(message = "la dateEffet est obligatoire")
        @FutureOrPresent(message = "la dateEffet ne peut pas etre dans le passe")
        LocalDate dateEffet
) {
}

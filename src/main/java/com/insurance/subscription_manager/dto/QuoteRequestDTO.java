package com.insurance.subscription_manager.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Incoming payload to create a quote.
 * Matches the spec example: { clientId, produitId, montant }.
 * Note: the status is NEVER accepted from the client — it is derived by the
 * business rule (&lt;= 10 000 -&gt; APPROVED, &gt; 10 000 -&gt; PENDING_MANAGER).
 */
public record QuoteRequestDTO(

        @NotNull(message = "clientId est obligatoire")
        Long clientId,

        @NotNull(message = "produitId est obligatoire")
        Long produitId,

        @NotNull(message = "le montant est obligatoire")
        @Positive(message = "le montant doit etre strictement positif")
        BigDecimal montant
) {
}

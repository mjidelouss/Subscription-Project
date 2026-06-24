package com.insurance.subscription_manager.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.insurance.subscription_manager.enums.QuoteStatus;

/**
 * Outgoing view of a quote. Flattens client/product references into ids + labels
 * so the front-end can render lists without extra round-trips.
 */
public record QuoteResponseDTO(
        Long id,
        Long clientId,
        String clientNom,
        Long produitId,
        String produitLibelle,
        BigDecimal montant,
        QuoteStatus statut,
        LocalDateTime dateCreation
) {
}

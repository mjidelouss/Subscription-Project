package com.insurance.subscription_manager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.insurance.subscription_manager.enums.ContractStatus;

public record ContractResponseDTO(
        Long id,
        Long quoteId,
        String numeroContrat,
        LocalDate dateEffet,
        ContractStatus statut,
        String clientNom,
        BigDecimal montant
) {
}

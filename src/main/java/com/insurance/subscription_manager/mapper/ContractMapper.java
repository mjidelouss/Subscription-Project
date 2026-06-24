package com.insurance.subscription_manager.mapper;

import org.springframework.stereotype.Component;

import com.insurance.subscription_manager.dto.ContractResponseDTO;
import com.insurance.subscription_manager.entity.Contract;

@Component
public class ContractMapper {

    public ContractResponseDTO toResponse(Contract contract) {
        return new ContractResponseDTO(
                contract.getId(),
                contract.getQuote().getId(),
                contract.getNumeroContrat(),
                contract.getDateEffet(),
                contract.getStatut(),
                contract.getQuote().getClient().getNom(),
                contract.getQuote().getMontant()
        );
    }
}

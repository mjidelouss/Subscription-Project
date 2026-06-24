package com.insurance.subscription_manager.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.insurance.subscription_manager.dto.ContractCreateDTO;
import com.insurance.subscription_manager.dto.ContractResponseDTO;
import com.insurance.subscription_manager.entity.Contract;
import com.insurance.subscription_manager.entity.Quote;
import com.insurance.subscription_manager.enums.ContractStatus;
import com.insurance.subscription_manager.enums.QuoteStatus;
import com.insurance.subscription_manager.exception.BusinessRuleException;
import com.insurance.subscription_manager.exception.DuplicateResourceException;
import com.insurance.subscription_manager.exception.ResourceNotFoundException;
import com.insurance.subscription_manager.mapper.ContractMapper;
import com.insurance.subscription_manager.repository.ContractRepository;
import com.insurance.subscription_manager.repository.QuoteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final QuoteRepository quoteRepository;
    private final ContractMapper contractMapper;

    @Transactional
    public ContractResponseDTO create(ContractCreateDTO request) {
        // 1. The quote must exist.
        Quote quote = quoteRepository.findById(request.quoteId())
                .orElseThrow(() -> new ResourceNotFoundException("Devis", request.quoteId()));

        // 2. The quote must be APPROVED (rejected / pending / draft are refused).
        if (quote.getStatut() != QuoteStatus.APPROVED) {
            throw new BusinessRuleException(
                    "Un contrat ne peut etre cree que depuis un devis APPROVED (statut actuel: "
                            + quote.getStatut() + ")");
        }

        // 3. A quote can only ever produce one contract.
        if (contractRepository.existsByQuoteId(quote.getId())) {
            throw new DuplicateResourceException(
                    "Un contrat existe deja pour le devis " + quote.getId());
        }

        Contract contract = Contract.builder()
                .quote(quote)
                .numeroContrat(generateContractNumber())
                .dateEffet(request.dateEffet())
                .statut(ContractStatus.CREATED)
                .build();

        return contractMapper.toResponse(contractRepository.save(contract));
    }

    @Transactional(readOnly = true)
    public List<ContractResponseDTO> findAll() {
        return contractRepository.findAll().stream()
                .map(contractMapper::toResponse)
                .toList();
    }

    private String generateContractNumber() {
        return "CTR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

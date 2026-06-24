package com.insurance.subscription_manager.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.insurance.subscription_manager.dto.QuoteRequestDTO;
import com.insurance.subscription_manager.dto.QuoteResponseDTO;
import com.insurance.subscription_manager.entity.Client;
import com.insurance.subscription_manager.entity.Product;
import com.insurance.subscription_manager.entity.Quote;
import com.insurance.subscription_manager.enums.QuoteStatus;
import com.insurance.subscription_manager.exception.BusinessRuleException;
import com.insurance.subscription_manager.exception.ResourceNotFoundException;
import com.insurance.subscription_manager.mapper.QuoteMapper;
import com.insurance.subscription_manager.repository.ClientRepository;
import com.insurance.subscription_manager.repository.ProductRepository;
import com.insurance.subscription_manager.repository.QuoteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuoteService {

    // Threshold above which a quote needs managerial approval.
    public static final BigDecimal MANAGER_APPROVAL_THRESHOLD = new BigDecimal("10000");

    private final QuoteRepository quoteRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final QuoteMapper quoteMapper;

    @Transactional
    public QuoteResponseDTO create(QuoteRequestDTO request) {
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", request.clientId()));
        Product product = productRepository.findById(request.produitId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit", request.produitId()));

        // Core business rule:
        //   montant <= 10 000 -> APPROVED automatically
        //   montant >  10 000 -> PENDING_MANAGER (needs approval before a contract)
        QuoteStatus statut = request.montant().compareTo(MANAGER_APPROVAL_THRESHOLD) <= 0
                ? QuoteStatus.APPROVED
                : QuoteStatus.PENDING_MANAGER;

        Quote quote = Quote.builder()
                .client(client)
                .product(product)
                .montant(request.montant())
                .statut(statut)
                .build();

        return quoteMapper.toResponse(quoteRepository.save(quote));
    }

    @Transactional
    public QuoteResponseDTO approve(Long quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new ResourceNotFoundException("Devis", quoteId));

        // Only a quote awaiting managerial approval can be approved.
        if (quote.getStatut() != QuoteStatus.PENDING_MANAGER) {
            throw new BusinessRuleException(
                    "Seul un devis en statut PENDING_MANAGER peut etre approuve (statut actuel: "
                            + quote.getStatut() + ")");
        }
        quote.setStatut(QuoteStatus.APPROVED);
        return quoteMapper.toResponse(quote);
    }

    @Transactional(readOnly = true)
    public List<QuoteResponseDTO> findAll(QuoteStatus statutFilter) {
        List<Quote> quotes = (statutFilter == null)
                ? quoteRepository.findAll()
                : quoteRepository.findByStatut(statutFilter);
        return quotes.stream().map(quoteMapper::toResponse).toList();
    }
}

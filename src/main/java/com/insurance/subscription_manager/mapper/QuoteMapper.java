package com.insurance.subscription_manager.mapper;

import org.springframework.stereotype.Component;

import com.insurance.subscription_manager.dto.QuoteResponseDTO;
import com.insurance.subscription_manager.entity.Quote;

@Component
public class QuoteMapper {

    // Only a response mapper here: the entity is built by the service because it
    // needs to resolve Client/Product references and apply the business rule.
    public QuoteResponseDTO toResponse(Quote quote) {
        return new QuoteResponseDTO(
                quote.getId(),
                quote.getClient().getId(),
                quote.getClient().getNom(),
                quote.getProduct().getId(),
                quote.getProduct().getLibelle(),
                quote.getMontant(),
                quote.getStatut(),
                quote.getDateCreation()
        );
    }
}

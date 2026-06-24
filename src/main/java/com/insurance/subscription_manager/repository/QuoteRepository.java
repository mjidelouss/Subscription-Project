package com.insurance.subscription_manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.insurance.subscription_manager.entity.Quote;
import com.insurance.subscription_manager.enums.QuoteStatus;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    // Useful query #1 from the spec: "liste des devis en attente de validation".
    // Also reused as the optional status filter on GET /api/quotes.
    List<Quote> findByStatut(QuoteStatus statut);
}

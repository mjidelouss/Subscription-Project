package com.insurance.subscription_manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.insurance.subscription_manager.entity.Contract;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    // Guard: prevents creating two contracts from the same quote.
    boolean existsByQuoteId(Long quoteId);

    // Useful query #2 from the spec: "nombre de contrats par client".
    @Query("""
            SELECT c.quote.client.id AS clientId,
                   c.quote.client.nom AS clientNom,
                   COUNT(c) AS contractCount
            FROM Contract c
            GROUP BY c.quote.client.id, c.quote.client.nom
            ORDER BY COUNT(c) DESC
            """)
    List<ContractCountByClient> countContractsByClient();

    /** Interface-based projection for the contracts-per-client aggregation. */
    interface ContractCountByClient {
        Long getClientId();
        String getClientNom();
        Long getContractCount();
    }
}

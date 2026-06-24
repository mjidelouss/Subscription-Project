package com.insurance.subscription_manager.enums;

/**
 * Lifecycle of a quote (devis).
 *
 * DRAFT           -> initial transient state (not really persisted in this flow, kept for completeness)
 * PENDING_MANAGER -> amount &gt; 10 000 EUR, waiting for managerial approval
 * APPROVED        -> ready to be converted into a contract
 * REJECTED        -> refused by a manager, cannot become a contract
 */
public enum QuoteStatus {
    DRAFT,
    PENDING_MANAGER,
    APPROVED,
    REJECTED
}

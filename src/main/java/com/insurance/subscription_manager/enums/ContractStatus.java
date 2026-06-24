package com.insurance.subscription_manager.enums;

/**
 * Lifecycle of a contract (contrat).
 * Kept minimal for the test: a contract is CREATED then can be ACTIVE.
 */
public enum ContractStatus {
    CREATED,
    ACTIVE,
    CANCELLED
}

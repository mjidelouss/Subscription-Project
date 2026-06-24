package com.insurance.subscription_manager.exception;

/**
 * Thrown when a functional/business rule is violated, e.g. trying to create a
 * contract from a quote that is not APPROVED. Mapped to HTTP 422.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}

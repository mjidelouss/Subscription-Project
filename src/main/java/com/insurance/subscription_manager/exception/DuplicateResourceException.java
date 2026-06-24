package com.insurance.subscription_manager.exception;

/** Thrown on a uniqueness violation (e.g. email already used). Mapped to HTTP 409. */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}

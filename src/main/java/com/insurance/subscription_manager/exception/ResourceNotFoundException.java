package com.insurance.subscription_manager.exception;

/** Thrown when a referenced entity does not exist. Mapped to HTTP 404. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " introuvable pour l'id " + id);
    }
}

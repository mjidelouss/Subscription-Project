package com.insurance.subscription_manager.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Uniform error body returned by the API.
 * `fieldErrors` is only populated for validation (400) responses.
 */
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors
) {
}

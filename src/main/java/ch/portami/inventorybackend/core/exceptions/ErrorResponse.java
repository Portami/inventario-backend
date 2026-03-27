package ch.portami.inventorybackend.core.exceptions;

import java.time.Instant;

/**
 * Standardised error body returned by the API.
 */
public record ErrorResponse(
        int status,
        String message,
        Instant timestamp
) {
    /** Convenience factory so callers don't have to supply the timestamp. */
    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(status, message, Instant.now());
    }
}

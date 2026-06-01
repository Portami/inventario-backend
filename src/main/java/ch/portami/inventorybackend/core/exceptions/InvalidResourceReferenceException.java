package ch.portami.inventorybackend.core.exceptions;

/**
 * Thrown when a request references another resource by id that does not exist (e.g. creating an
 * entity that points at a non-existent parent).
 *
 * <p>Mapped to HTTP 422 (Unprocessable Content) by {@link GlobalExceptionHandler}. Domain modules
 * extend this with resource-specific subtypes (e.g. {@code InvalidSupplierReferenceException}).
 */
public class InvalidResourceReferenceException extends ResourceSpecificException {

    public InvalidResourceReferenceException(ResourceIdentifier... identifiers) {
        super(identifiers);
    }

    public InvalidResourceReferenceException(String message, ResourceIdentifier... identifiers) {
        super(message, identifiers);
    }

    public InvalidResourceReferenceException(String message, Throwable cause, ResourceIdentifier... identifiers) {
        super(message, cause, identifiers);
    }

    public InvalidResourceReferenceException(Throwable cause, ResourceIdentifier... identifiers) {
        super(cause, identifiers);
    }

    public InvalidResourceReferenceException(String message, boolean enableSuppression,
            boolean writableStackTrace, Throwable cause, ResourceIdentifier... identifiers) {
        super(message, enableSuppression, writableStackTrace, cause, identifiers);
    }
}

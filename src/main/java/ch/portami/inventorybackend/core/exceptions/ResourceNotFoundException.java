package ch.portami.inventorybackend.core.exceptions;

/**
 * Thrown when a requested resource cannot be found, typically when looking it up by its identifier.
 *
 * <p>Mapped to HTTP 404 (Not Found) by {@link GlobalExceptionHandler}. Domain modules extend this
 * with resource-specific subtypes (e.g. {@code FeltNotFoundException}).
 */
public class ResourceNotFoundException extends ResourceSpecificException {

    public ResourceNotFoundException(ResourceIdentifier... identifiers) {
        super(identifiers);
    }

    public ResourceNotFoundException(String message, ResourceIdentifier... identifiers) {
        super(message, identifiers);
    }

    public ResourceNotFoundException(String message, Throwable cause, ResourceIdentifier... identifiers) {
        super(message, cause, identifiers);
    }

    public ResourceNotFoundException(Throwable cause, ResourceIdentifier... identifiers) {
        super(cause, identifiers);
    }

    public ResourceNotFoundException(String message, boolean enableSuppression,
            boolean writableStackTrace, Throwable cause, ResourceIdentifier... identifiers) {
        super(message, enableSuppression, writableStackTrace, cause, identifiers);
    }
}

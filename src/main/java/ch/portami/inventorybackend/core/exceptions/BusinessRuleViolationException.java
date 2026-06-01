package ch.portami.inventorybackend.core.exceptions;

/**
 * Thrown when an operation would violate a domain business rule even though the request itself is
 * well-formed (e.g. an inventory operation that is not allowed in the current state).
 *
 * <p>Mapped to HTTP 409 (Conflict) by {@link GlobalExceptionHandler}.
 */
public class BusinessRuleViolationException extends ResourceSpecificException {

    public BusinessRuleViolationException(ResourceIdentifier... identifiers) {
        super(identifiers);
    }

    public BusinessRuleViolationException(String message, ResourceIdentifier... identifiers) {
        super(message, identifiers);
    }

    public BusinessRuleViolationException(String message, Throwable cause, ResourceIdentifier... identifiers) {
        super(message, cause, identifiers);
    }

    public BusinessRuleViolationException(Throwable cause, ResourceIdentifier... identifiers) {
        super(cause, identifiers);
    }

    public BusinessRuleViolationException(String message, boolean enableSuppression, boolean writableStackTrace,
            Throwable cause, ResourceIdentifier... identifiers) {
        super(message, enableSuppression, writableStackTrace, cause, identifiers);
    }
}

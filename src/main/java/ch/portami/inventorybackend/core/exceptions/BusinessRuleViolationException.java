package ch.portami.inventorybackend.core.exceptions;

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

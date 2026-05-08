package ch.portami.inventorybackend.core.exceptions;

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

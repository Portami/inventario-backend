package ch.portami.inventorybackend.core.exceptions;

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

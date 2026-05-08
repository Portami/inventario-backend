package ch.portami.inventorybackend.core.exceptions;

import java.util.List;

public abstract class ResourceSpecificException extends RuntimeException {

    private final List<ResourceIdentifier> resourceIdentifiers;

    protected ResourceSpecificException(ResourceIdentifier... identifiers) {
        this.resourceIdentifiers = List.of(identifiers);
    }

    protected ResourceSpecificException(String message, ResourceIdentifier... identifiers) {
        super(message);
        this.resourceIdentifiers = List.of(identifiers);
    }

    protected ResourceSpecificException(String message, Throwable cause, ResourceIdentifier... identifiers) {
        super(message, cause);
        this.resourceIdentifiers = List.of(identifiers);
    }

    protected ResourceSpecificException(Throwable cause, ResourceIdentifier... identifiers) {
        super(cause);
        this.resourceIdentifiers = List.of(identifiers);
    }

    protected ResourceSpecificException(String message, boolean enableSuppression,
            boolean writableStackTrace, Throwable cause, ResourceIdentifier... identifiers) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.resourceIdentifiers = List.of(identifiers);
    }

    public List<ResourceIdentifier> getResourceIdentifiers() {
        return resourceIdentifiers;
    }
}

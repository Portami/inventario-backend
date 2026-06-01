package ch.portami.inventorybackend.core.exceptions;

import java.io.Serial;
import java.util.List;

/**
 * Base class for exceptions that concern a specific domain resource (or set of resources).
 *
 * <p>Each instance carries the {@link ResourceIdentifier}s of the resource(s) involved, so that
 * {@link GlobalExceptionHandler} can expose them as structured properties on the error response.
 * Concrete subclasses ({@link ResourceNotFoundException}, {@link InvalidResourceReferenceException},
 * {@link BusinessRuleViolationException}) map to specific HTTP statuses.
 */
public abstract class ResourceSpecificException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
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

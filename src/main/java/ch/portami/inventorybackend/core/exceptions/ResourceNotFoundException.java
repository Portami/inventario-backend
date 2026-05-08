package ch.portami.inventorybackend.core.exceptions;

public class ResourceNotFoundException extends ResourceSpecificException {

    public ResourceNotFoundException(String resourceType, Object resourceId) {
        super(resourceType, resourceId);
    }

    public ResourceNotFoundException(String message, String resourceType, Object resourceId) {
        super(message, resourceType, resourceId);
    }

    public ResourceNotFoundException(String message, String resourceType, Object resourceId, Throwable cause) {
        super(message, resourceType, resourceId, cause);
    }

    public ResourceNotFoundException(String resourceType, Object resourceId, Throwable cause) {
        super(resourceType, resourceId, cause);
    }

    public ResourceNotFoundException(String message, String resourceType, Object resourceId, boolean enableSuppression,
            boolean writableStackTrace, Throwable cause) {
        super(message, resourceType, resourceId, enableSuppression, writableStackTrace, cause);
    }
}

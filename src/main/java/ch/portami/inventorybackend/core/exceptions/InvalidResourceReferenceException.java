package ch.portami.inventorybackend.core.exceptions;

public class InvalidResourceReferenceException extends ResourceSpecificException {

    public InvalidResourceReferenceException(String resourceType, Object resourceId) {
        super(resourceType, resourceId);
    }

    public InvalidResourceReferenceException(String message, String resourceType, Object resourceId) {
        super(message, resourceType, resourceId);
    }

    public InvalidResourceReferenceException(String message, String resourceType, Object resourceId, Throwable cause) {
        super(message, resourceType, resourceId, cause);
    }

    public InvalidResourceReferenceException(String resourceType, Object resourceId, Throwable cause) {
        super(resourceType, resourceId, cause);
    }

    public InvalidResourceReferenceException(String message, String resourceType, Object resourceId,
            boolean enableSuppression, boolean writableStackTrace, Throwable cause) {
        super(message, resourceType, resourceId, enableSuppression, writableStackTrace, cause);
    }
}

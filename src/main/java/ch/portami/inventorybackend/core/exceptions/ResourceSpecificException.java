package ch.portami.inventorybackend.core.exceptions;

public abstract class ResourceSpecificException extends RuntimeException {

    protected final String resourceType;
    protected final Object resourceId;

    protected ResourceSpecificException(String resourceType, Object resourceId) {
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    protected ResourceSpecificException(String message, String resourceType, Object resourceId) {
        super(message);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    protected ResourceSpecificException(String message, String resourceType, Object resourceId, Throwable cause) {
        super(message, cause);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    protected ResourceSpecificException(String resourceType, Object resourceId, Throwable cause) {
        super(cause);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    protected ResourceSpecificException(String message,
            String resourceType, Object resourceId, boolean enableSuppression,
            boolean writableStackTrace, Throwable cause) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    protected String getResourceType() {
        return resourceType;
    }

    protected Object getResourceId() {
        return resourceId;
    }
}

package ch.portami.inventorybackend.storage.exception;

import ch.portami.inventorybackend.core.exceptions.InvalidResourceReferenceException;
import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import java.text.MessageFormat;

/**
 * Thrown when a request references a storage location by an id that does not correspond to an
 * existing storage.
 */
public class InvalidStorageReferenceException extends InvalidResourceReferenceException {

    public InvalidStorageReferenceException(Long storageId) {
        super(MessageFormat.format("The request is referencing a storage with id {0} that does not exists", storageId),
                new ResourceIdentifier("storageId", storageId));
    }
}

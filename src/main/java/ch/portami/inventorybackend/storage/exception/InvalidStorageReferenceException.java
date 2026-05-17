package ch.portami.inventorybackend.storage.exception;

import ch.portami.inventorybackend.core.exceptions.InvalidResourceReferenceException;
import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import java.text.MessageFormat;

public class InvalidStorageReferenceException extends InvalidResourceReferenceException {

    public InvalidStorageReferenceException(Long storageId) {
        super(MessageFormat.format("The request is referencing a storage with id {0} that does not exists", storageId), new ResourceIdentifier("storageId", storageId));
    }
}

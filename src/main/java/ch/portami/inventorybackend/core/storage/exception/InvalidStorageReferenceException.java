package ch.portami.inventorybackend.core.storage.exception;

import ch.portami.inventorybackend.core.exceptions.InvalidResourceReferenceException;
import java.text.MessageFormat;

public class InvalidStorageReferenceException extends InvalidResourceReferenceException {

    public InvalidStorageReferenceException(Long storageId) {
        super(MessageFormat.format("The request is referencing a storage with id {0} that does not exists", storageId), "storageId", storageId);
    }
}

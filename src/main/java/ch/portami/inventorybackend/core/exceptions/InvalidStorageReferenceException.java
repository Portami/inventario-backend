package ch.portami.inventorybackend.core.exceptions;

public class InvalidStorageReferenceException extends InvalidResourceReferenceException {

    public InvalidStorageReferenceException(Long storageId) {
        super("The request is referencing a storage with id %d that does not exists".formatted(storageId));
    }

}

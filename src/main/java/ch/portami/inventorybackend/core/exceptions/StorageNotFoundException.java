package ch.portami.inventorybackend.core.exceptions;

public class StorageNotFoundException extends ResourceNotFoundException {

    public StorageNotFoundException(long storageId) {
        super("Storage with id %d not found".formatted(storageId));
    }

}

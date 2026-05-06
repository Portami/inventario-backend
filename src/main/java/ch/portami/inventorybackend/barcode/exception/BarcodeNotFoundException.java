package ch.portami.inventorybackend.barcode.exception;

import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;

public class BarcodeNotFoundException extends ResourceNotFoundException {

    public BarcodeNotFoundException(long barcodeId) {
        super("Barcode not found");
    }

}

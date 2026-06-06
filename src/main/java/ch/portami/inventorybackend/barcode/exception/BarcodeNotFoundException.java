package ch.portami.inventorybackend.barcode.exception;

import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;

/**
 * Thrown when a scanned barcode value does not correspond to any existing barcode.
 */
public class BarcodeNotFoundException extends ResourceNotFoundException {

    public BarcodeNotFoundException(long barcodeId) {
        super("Barcode not found", new ResourceIdentifier("barcodeId", barcodeId));
    }
}

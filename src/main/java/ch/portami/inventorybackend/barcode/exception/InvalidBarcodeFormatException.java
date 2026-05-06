package ch.portami.inventorybackend.barcode.exception;

import ch.portami.inventorybackend.core.exceptions.InvalidInputException;

public class InvalidBarcodeFormatException extends InvalidInputException {

    public InvalidBarcodeFormatException(String message) {
        super(message);
    }

}

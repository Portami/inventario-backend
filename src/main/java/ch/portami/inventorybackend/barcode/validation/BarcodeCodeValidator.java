package ch.portami.inventorybackend.barcode.validation;

import ch.portami.inventorybackend.barcode.BarcodeCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BarcodeCodeValidator implements ConstraintValidator<ValidBarcodeCode, BarcodeCode> {

    @Override
    public boolean isValid(BarcodeCode barcodeCode, ConstraintValidatorContext context) {
        if (barcodeCode == null || barcodeCode.value() == null || barcodeCode.value().isBlank()) {
            return false;
        }

        try {
            return Long.parseLong(barcodeCode.value()) > 0;
        } catch (NumberFormatException _) {
            return false;
        }
    }
}

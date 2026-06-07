package ch.portami.inventorybackend.barcode.validation;

import ch.portami.inventorybackend.barcode.BarcodeCode;
import ch.portami.inventorybackend.core.validation.ConstraintViolations;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator backing {@link ValidBarcodeCode}: rejects barcode codes that are null, blank,
 * non-numeric, or not a positive number, reporting a specific message for each case.
 */
public class BarcodeCodeValidator implements ConstraintValidator<ValidBarcodeCode, BarcodeCode> {

    @Override
    public boolean isValid(BarcodeCode barcodeCode, ConstraintValidatorContext context) {
        if (barcodeCode == null || barcodeCode.value() == null) {
            return ConstraintViolations.reject(context, "Barcode must not be null");
        }

        if (barcodeCode.value()
                       .isBlank()) {
            return ConstraintViolations.reject(context, "Barcode must not be blank");
        }

        try {
            long parsed = Long.parseLong(barcodeCode.value());
            if (parsed <= 0) {
                return ConstraintViolations.reject(context, "Barcode must be a positive number");
            }
        } catch (NumberFormatException _) {
            return ConstraintViolations.reject(context, "Barcode must be numeric");
        }

        return true;
    }
}

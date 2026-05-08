package ch.portami.inventorybackend.barcode.validation;

import ch.portami.inventorybackend.barcode.BarcodeCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BarcodeCodeValidator implements ConstraintValidator<ValidBarcodeCode, BarcodeCode> {

    @Override
    public boolean isValid(BarcodeCode barcodeCode, ConstraintValidatorContext context) {
        if (barcodeCode == null || barcodeCode.value() == null) {
            addViolation(context, "Barcode must not be null");
            return false;
        }

        if (barcodeCode.value().isBlank()) {
            addViolation(context, "Barcode must not be blank");
            return false;
        }

        try {
            long parsed = Long.parseLong(barcodeCode.value());
            if (parsed <= 0) {
                addViolation(context, "Barcode must be a positive number");
                return false;
            }
        } catch (NumberFormatException _) {
            addViolation(context, "Barcode must be numeric");
            return false;
        }

        return true;
    }

    private void addViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
               .addConstraintViolation();
    }
}

package ch.portami.inventorybackend.barcode.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bean Validation constraint asserting that a {@link ch.portami.inventorybackend.barcode.BarcodeCode}
 * holds a non-blank, positive numeric value. Validated by {@link BarcodeCodeValidator}.
 */
@Documented
@Constraint(validatedBy = BarcodeCodeValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBarcodeCode {

    String message() default "Code must be a valid barcode code";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

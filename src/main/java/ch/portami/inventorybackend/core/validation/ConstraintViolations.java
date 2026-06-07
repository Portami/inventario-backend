package ch.portami.inventorybackend.core.validation;

import jakarta.validation.ConstraintValidatorContext;

/**
 * Helpers for reporting custom messages from {@link jakarta.validation.ConstraintValidator}
 * implementations.
 *
 * <p>A validator that wants a message other than the constraint's default has to disable the default
 * violation and build a templated one — a small but easy-to-get-wrong dance. These helpers centralise
 * it so validators stay focused on their actual logic.
 *
 * <p>This class cannot be instantiated.
 */
public final class ConstraintViolations {

    private ConstraintViolations() {

    }

    /**
     * Replaces the constraint's default violation with a single violation carrying the given message.
     *
     * @param context the validator context to record the violation on
     * @param message the message to report instead of the default
     */
    public static void addViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
               .addConstraintViolation();
    }

    /**
     * Records a custom-message violation (see {@link #addViolation}) and returns {@code false}, so a
     * validator can fail in a single statement:
     * {@snippet : return ConstraintViolations.reject(context, "must be positive"); }
     *
     * @param context the validator context to record the violation on
     * @param message the message to report instead of the default
     * @return always {@code false}, to be returned directly from {@code isValid}
     */
    public static boolean reject(ConstraintValidatorContext context, String message) {
        addViolation(context, message);
        return false;
    }
}

package ch.portami.inventorybackend.felt.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validates that a scrap-piece side (length or width) is at least the configured minimum scrap side
 * ({@code portami.felt.scrap-min-side-cm}). A {@code null} value passes — combine with
 * {@code @NotNull} where the side is mandatory. The threshold is resolved at runtime from
 * {@link ch.portami.inventorybackend.felt.config.FeltProperties}, so it is the single source of truth
 * shared with the "Abschneiden" cut flow (which silently drops too-small offcuts instead of failing).
 */
@Documented
@Constraint(validatedBy = MinScrapSideValidator.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface MinScrapSide {

    String message() default "must be at least the configured minimum scrap side";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

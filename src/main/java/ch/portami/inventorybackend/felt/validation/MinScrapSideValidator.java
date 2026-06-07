package ch.portami.inventorybackend.felt.validation;

import ch.portami.inventorybackend.core.validation.ConstraintViolations;
import ch.portami.inventorybackend.felt.config.FeltProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Enforces {@link MinScrapSide} by comparing the value against the configured minimum scrap side. The
 * {@link FeltProperties} dependency is injected by Spring's validator factory, which makes constraint
 * validators Spring-managed beans.
 */
public class MinScrapSideValidator implements ConstraintValidator<MinScrapSide, Double> {

    private final FeltProperties feltProperties;

    public MinScrapSideValidator(FeltProperties feltProperties) {
        this.feltProperties = feltProperties;
    }

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // nullability is enforced separately by @NotNull where required
        }

        double minSide = feltProperties.scrapMinSideCm();
        if (value >= minSide) {
            return true;
        }

        return ConstraintViolations.reject(context, "must be at least " + minSide + " cm");
    }
}

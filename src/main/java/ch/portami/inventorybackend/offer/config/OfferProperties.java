package ch.portami.inventorybackend.offer.config;

import ch.portami.inventorybackend.offer.domain.OfferState;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties governing the offer lifecycle, bound from {@code portami.offer.*}.
 *
 * @param dueDays the number of days from "now" until an offer becomes due, keyed by the
 *                {@link OfferState} an offer transitions into. States absent from the map leave the
 *                due date unchanged on transition.
 */
@Validated
@ConfigurationProperties(prefix = "portami.offer")
public record OfferProperties(
        @NotNull Map<OfferState, @Positive Integer> dueDays) {
}

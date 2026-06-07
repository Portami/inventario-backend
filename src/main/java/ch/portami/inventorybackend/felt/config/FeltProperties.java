package ch.portami.inventorybackend.felt.config;

import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for felt inventory, bound from {@code portami.felt.*}.
 *
 * @param scrapMinSideCm the minimum side length, in centimetres, a scrap piece must have to be
 *                       worth tracking. It is the single source of truth for this threshold: creating
 *                       or updating a scrap piece with any side below this value is rejected (400),
 *                       and during an "Abschneiden" cut an offcut below it is silently dropped instead
 *                       of being saved.
 */
@Validated
@ConfigurationProperties(prefix = "portami.felt")
public record FeltProperties(
        @Positive double scrapMinSideCm) {
}
